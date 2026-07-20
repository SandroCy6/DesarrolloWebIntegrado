package cinerama.ventas.service;

import cinerama.ventas.client.CatalogoClient;
import cinerama.ventas.client.ClienteClient;
import cinerama.ventas.client.PromocionClient;

import cinerama.ventas.client.NotificacionClient;
import cinerama.ventas.dto.AsientoDTO;
import cinerama.ventas.dto.ClienteRequestDTO;
import cinerama.ventas.dto.DetalleRequestDTO;
import cinerama.ventas.dto.VentaRequestDTO;
import cinerama.ventas.model.DetalleVenta;
import cinerama.ventas.model.Venta;
import cinerama.ventas.repository.VentaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VentaService {
    private final NotificacionClient notificacionClient;
    private final VentaRepository ventaRepository;
    private final CatalogoClient catalogoClient;
    private final PagoService pagoService;
    private final PromocionClient promocionClient;
    private final ClienteClient clienteClient;

    public VentaService(VentaRepository ventaRepository,
            NotificacionClient notificacionClient,
            CatalogoClient catalogoClient,
            PagoService pagoService,
            PromocionClient promocionClient,
            ClienteClient clienteClient) {
        this.ventaRepository = ventaRepository;
        this.notificacionClient = notificacionClient;
        this.catalogoClient = catalogoClient;
        this.pagoService = pagoService;
        this.promocionClient = promocionClient;
        this.clienteClient = clienteClient;//
    }

    public Venta registrarVenta(VentaRequestDTO request) {
        // 1. Validar máximo de entradas
        int totalEntradas = request.getDetalles().stream()
                .filter(d -> d.getTipoItem().equalsIgnoreCase("ENTRADA"))
                .mapToInt(DetalleRequestDTO::getCantidad)
                .sum();

        if (totalEntradas > 10) {
            throw new IllegalArgumentException("Máximo 10 entradas por compra.");
        }

        // 2. Crear la Venta
        Venta venta = new Venta();
        venta.setClienteDni(request.getClienteDni());
        venta.setClienteCorreo(request.getClienteCorreo());
        venta.setClienteCelular(request.getClienteCelular());
        venta.setClienteNombre(request.getClienteNombre());
        venta.setFecha(LocalDateTime.now());
        venta.setMetodoPago(request.getMetodoPago());
        venta.setEstadoPago("PENDIENTE");
        venta.setDetalles(new ArrayList<>());

        BigDecimal precioBaseHorario = BigDecimal.ZERO;

        // 3. Obtener información del Horario
        if (request.getHorarioId() != null) {
            try {
                cinerama.ventas.dto.HorarioResponseDTO h = catalogoClient.obtenerHorario(request.getHorarioId())
                        .orElse(null);
                if (h != null) {
                    venta.setTituloPelicula(h.getTituloPelicula());
                    venta.setSala("Sala " + h.getNumeroSala() + " — " + h.getNombreCine());

                    if (h.getPrecio() != null) {
                        precioBaseHorario = BigDecimal.valueOf(h.getPrecio());
                    }
                }
            } catch (Exception e) {
                System.err.println("⚠️ No se pudo obtener horario: " + e.getMessage());
            }
        }

        BigDecimal totalVenta = BigDecimal.ZERO;

        // 4. Procesar Detalles (Aquí ya NO validamos los asientos, solo calculamos
        // precios)
        for (DetalleRequestDTO detReq : request.getDetalles()) {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setTipoItem(detReq.getTipoItem().toUpperCase());
            detalle.setItemId(detReq.getItemId()); // Esto guarda el 10 (Horario), lo cual está bien para tu historial
            detalle.setCantidad(detReq.getCantidad());

            BigDecimal precioSeguro = detReq.getPrecioUnitario();

            if (detalle.getTipoItem().equals("ENTRADA")) {
                if (precioBaseHorario.compareTo(BigDecimal.ZERO) == 0) {
                    throw new IllegalStateException(
                            "Error: El horario seleccionado no tiene un precio válido configurado.");
                }
                precioSeguro = precioBaseHorario; // Usamos el precio del horario
            }

            detalle.setPrecioUnitario(precioSeguro);
            BigDecimal subtotal = precioSeguro.multiply(new BigDecimal(detReq.getCantidad()));
            detalle.setSubtotal(subtotal);
            detalle.setVenta(venta);

            venta.getDetalles().add(detalle);
            totalVenta = totalVenta.add(subtotal);
        }

        // 5. NUEVA VALIDACIÓN: Verificar que los ASIENTOS REALES estén disponibles
        // antes de cobrar
        if (request.getAsientosIds() != null && !request.getAsientosIds().isEmpty()) {
            for (Long asientoId : request.getAsientosIds()) {
                try {
                    AsientoDTO asiento = catalogoClient.obtenerAsiento(asientoId)
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "El asiento con ID " + asientoId + " no existe."));

                    if ("OCUPADO".equalsIgnoreCase(asiento.getEstado())) {
                        throw new IllegalArgumentException("El asiento " + asiento.getNumero()
                                + " ya se encuentra ocupado. Por favor, seleccione otro.");
                    }
                } catch (IllegalArgumentException e) {
                    throw e; // Relanzamos si es ocupado o no existe
                } catch (Exception e) {
                    System.err.println("🔴 ERROR REAL DE FEIGN (Validación): " + e.getMessage());
                    throw new IllegalStateException(
                            "Error de comunicación con el Catálogo. No se pudo validar la disponibilidad de los asientos.");
                }
            }
        }

        venta.setTotal(totalVenta);
        venta.setDescuentoAplicado(BigDecimal.ZERO);
        venta.setCodigoPromo(request.getCodigoPromo());

        // 6. Aplicar Promociones
        if (request.getCodigoPromo() != null && !request.getCodigoPromo().isEmpty()) {
            try {
                cinerama.ventas.dto.ValidarPromoRequestDTO promoReq = new cinerama.ventas.dto.ValidarPromoRequestDTO(
                        request.getCodigoPromo());
                cinerama.ventas.dto.PromocionResponseDTO promoResponse = promocionClient.validarPromocion(promoReq);

                if (promoResponse != null && promoResponse.getEsValida()) {
                    BigDecimal porcentajeDescuento = promoResponse.getDescuento();
                    BigDecimal dineroADescontar = totalVenta.multiply(porcentajeDescuento)
                            .divide(new BigDecimal("100"));

                    totalVenta = totalVenta.subtract(dineroADescontar);
                    venta.setTotal(totalVenta);
                    venta.setDescuentoAplicado(dineroADescontar);
                } else {
                    System.out.println("⚠️ El código promocional no es válido o ya expiró.");
                }
            } catch (Exception e) {
                System.err.println("⚠️ No se pudo conectar con el servicio de promociones: " + e.getMessage());
            }
        }

        String codigo = UUID.randomUUID().toString();
        venta.setCodigoQr(codigo);

        // 7. Cobrar en MercadoPago
        boolean pagoExitoso = pagoService.procesarPago(
                totalVenta,
                request.getMetodoPago(),
                request.getTokenTarjeta(),
                request.getClienteCorreo());

        if (!pagoExitoso) {
            throw new IllegalArgumentException("El pago fue rechazado por Mercado Pago");
        }

        if (pagoService.isPagoSimulado()) {
            venta.setEstadoPago("APROBADO_SIMULADO");
        } else {
            venta.setEstadoPago("APROBADO");
        }
        Venta saved = ventaRepository.save(venta);

        // 8. Ocupar los asientos definitivamente después de pagar
        if (request.getAsientosIds() != null && !request.getAsientosIds().isEmpty()) {
            String asientosTexto = request.getAsientosIds().stream()
                    .map(id -> {
                        try {
                            catalogoClient.ocuparAsiento(id, "\"OCUPADO\"");
                            return catalogoClient.obtenerAsiento(id)
                                    .map(AsientoDTO::getNumero)
                                    .orElse("A" + id);
                        } catch (Exception e) {
                            System.err.println("⚠️ Falló la comunicación con Catálogo para ocupar el asiento ID " + id
                                    + ": " + e.getMessage());
                            return "A" + id;
                        }
                    })
                    .collect(Collectors.joining(", "));
            saved.setAsientos(asientosTexto);
            ventaRepository.save(saved);
        }
        // PASO 8.5: Sincronizar con el microservicio de Clientes
        try {
            ClienteRequestDTO clienteReq = new ClienteRequestDTO();
            clienteReq.setDni(request.getClienteDni());
            clienteReq.setNombre(request.getClienteNombre());
            clienteReq.setCorreo(request.getClienteCorreo());

            // Verificamos que no sea nulo antes de setearlo
            if (request.getClienteCelular() != null) {
                clienteReq.setTelefono(request.getClienteCelular());
            }

            clienteClient.verificarORegistrar(clienteReq);
            System.out.println("✅ Cliente sincronizado exitosamente: " + request.getClienteDni());
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo sincronizar el cliente con el microservicio: " + e.getMessage());
        }

        // 9. Notificar al final
        notificacionClient.notificar(saved);
        return saved;
    }

    public Venta obtenerVentaPorId(Long id) {
        Optional<Venta> venta = ventaRepository.findById(id);
        if (venta.isEmpty()) {
            throw new IllegalArgumentException("Venta no encontrada con ID: " + id);
        }
        return venta.get();
    }

    public List<Venta> obtenerHistorialPorDni(String dni) {
        return ventaRepository.findByClienteDni(dni);
    }

    public Page<Venta> obtenerTodasLasVentas(Pageable pageable) {
        return ventaRepository.findAll(pageable);
    }

}

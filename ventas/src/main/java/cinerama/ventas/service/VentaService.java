package cinerama.ventas.service;

import cinerama.ventas.client.CatalogoClient;
import cinerama.ventas.client.PromocionClient;

import cinerama.ventas.client.NotificacionClient;
import cinerama.ventas.dto.AsientoDTO;
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

    public VentaService(VentaRepository ventaRepository,
            NotificacionClient notificacionClient,
            CatalogoClient catalogoClient,
            PagoService pagoService,
             PromocionClient promocionClient) {
        this.ventaRepository = ventaRepository;
        this.notificacionClient = notificacionClient;
        this.catalogoClient = catalogoClient;
        this.pagoService = pagoService;
        this.promocionClient = promocionClient; //
    }

    public Venta registrarVenta(VentaRequestDTO request) {
        // Validar máximo de entradas
        int totalEntradas = request.getDetalles().stream()
                .filter(d -> d.getTipoItem().equalsIgnoreCase("ENTRADA"))
                .mapToInt(DetalleRequestDTO::getCantidad)
                .sum();

        if (totalEntradas > 10) {
            throw new IllegalArgumentException("Máximo 10 entradas por compra.");
        }

        // Crear la Venta
        Venta venta = new Venta();
        venta.setClienteDni(request.getClienteDni());
        venta.setClienteCorreo(request.getClienteCorreo());
        venta.setClienteCelular(request.getClienteCelular());
        venta.setClienteNombre(request.getClienteNombre());
        venta.setFecha(LocalDateTime.now()); // Fecha automática
        venta.setMetodoPago(request.getMetodoPago()); // Guardamos el metodo de pago
        venta.setEstadoPago("PENDIENTE"); // Estado inicial
        venta.setDetalles(new ArrayList<>());
        if (request.getHorarioId() != null) {
            try {
                catalogoClient.obtenerHorario(request.getHorarioId()).ifPresent(h -> {
                    venta.setTituloPelicula(h.getTituloPelicula());
                    venta.setSala("Sala " + h.getNumeroSala() + " — " + h.getNombreCine());
                });
            } catch (Exception e) {
                System.err.println("⚠️ No se pudo obtener horario: " + e.getMessage());
            }
        }
        BigDecimal totalVenta = BigDecimal.ZERO;

        // Procesar cada detalle y calcular el total
        for (DetalleRequestDTO detReq : request.getDetalles()) {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setTipoItem(detReq.getTipoItem().toUpperCase());
            detalle.setItemId(detReq.getItemId());
            detalle.setCantidad(detReq.getCantidad());

            BigDecimal precioSeguro = detReq.getPrecioUnitario();

            if (detalle.getTipoItem().equals("ENTRADA")) {
                try {
                    // Llamamos a Catálogo para obtener la información real del asiento
                    AsientoDTO asiento = catalogoClient.obtenerAsiento(detReq.getItemId())
                            .orElseThrow(() -> new IllegalArgumentException("El asiento con ID " + detReq.getItemId() + " no existe."));

                    // Verificar disponibilidad
                    if ("OCUPADO".equalsIgnoreCase(asiento.getEstado())) {
                        throw new IllegalArgumentException("El asiento " + asiento.getNumero() + " ya se encuentra ocupado. Por favor, seleccione otro.");
                    }

                    // Obtener precio desde catálogo (No confiamos en el precio del Request por seguridad)
                    precioSeguro = BigDecimal.valueOf(asiento.getPrecio());

                } catch (IllegalArgumentException e) {
                    throw e;
                } catch (Exception e) {
                    // Manejo de fallos de comunicación (Try/Catch básico)
                    throw new IllegalStateException("Error de comunicación con el Catálogo. No se pudo validar la disponibilidad de los asientos. Intente nuevamente.");
                }
            }

            detalle.setPrecioUnitario(precioSeguro);

            // Subtotal = cantidad * precio seguro
            BigDecimal subtotal = precioSeguro.multiply(new BigDecimal(detReq.getCantidad()));
            detalle.setSubtotal(subtotal);
            detalle.setVenta(venta);

            venta.getDetalles().add(detalle);
            totalVenta = totalVenta.add(subtotal);
        }

        venta.setTotal(totalVenta);
        venta.setDescuentoAplicado(BigDecimal.ZERO);
        venta.setCodigoPromo(request.getCodigoPromo());
        // Si el cliente envió un código promocional en la venta
        if (request.getCodigoPromo() != null && !request.getCodigoPromo().isEmpty()) {
            try {
                cinerama.ventas.dto.ValidarPromoRequestDTO promoReq = new cinerama.ventas.dto.ValidarPromoRequestDTO(request.getCodigoPromo());
                cinerama.ventas.dto.PromocionResponseDTO promoResponse = promocionClient.validarPromocion(promoReq);

                if (promoResponse != null && promoResponse.getEsValida()) {
                    BigDecimal porcentajeDescuento = promoResponse.getDescuento(); // Ej. 10.00 %

                    // Calculamos el dinero a descontar: (Total * Porcentaje) / 100
                    BigDecimal dineroADescontar = totalVenta.multiply(porcentajeDescuento)
                            .divide(new BigDecimal("100"));

                    // Restamos el descuento al total original
                    totalVenta = totalVenta.subtract(dineroADescontar);

                    // Actualizamos el objeto venta con los nuevos montos
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

        // Integración de MercadoPago
        boolean pagoExitoso = pagoService.procesarPago(
                totalVenta,
                request.getMetodoPago(),
                request.getTokenTarjeta(),
                request.getClienteCorreo()
        );

        if (!pagoExitoso) {
            // Rollback
            throw new IllegalArgumentException("El pago fue RECHAZADO por MercadoPago");
        }

        venta.setEstadoPago("APROBADO");

        // Guardar en BD
        Venta saved = ventaRepository.save(venta);

        // Marcar asientos como OCUPADO
        if (request.getAsientosIds() != null && !request.getAsientosIds().isEmpty()) {
            String asientosTexto = request.getAsientosIds().stream()
                    .map(id -> {
                        try {
                            catalogoClient.ocuparAsiento(id, "\"OCUPADO\"");

                            String numero = catalogoClient.obtenerAsiento(id)
                                    .map(AsientoDTO::getNumero)
                                    .orElse("A" + id);

                            return numero;
                        } catch (Exception e) {
                            System.err.println("⚠️ Falló la comunicación con Catálogo para ocupar el asiento ID " + id + ": " + e.getMessage());
                            return "A" + id;
                        }
                    })
                    .collect(Collectors.joining(", "));
            saved.setAsientos(asientosTexto);
            ventaRepository.save(saved);
        }

        // Notificar AL FINAL
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

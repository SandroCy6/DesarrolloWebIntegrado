package cinerama.ventas.service;

import cinerama.ventas.client.CatalogoClient;
import cinerama.ventas.client.NotificacionClient;
import cinerama.ventas.dto.AsientoDTO;
import cinerama.ventas.dto.DetalleRequestDTO;
import cinerama.ventas.dto.VentaRequestDTO;
import cinerama.ventas.model.DetalleVenta;
import cinerama.ventas.model.Venta;
import cinerama.ventas.repository.VentaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

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

    public VentaService(VentaRepository ventaRepository,
            NotificacionClient notificacionClient, CatalogoClient catalogoClient) {
        this.ventaRepository = ventaRepository;
        this.notificacionClient = notificacionClient;
        this.catalogoClient = catalogoClient;
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
            detalle.setPrecioUnitario(detReq.getPrecioUnitario());

            // Subtotal = cantidad * precio
            BigDecimal subtotal = detReq.getPrecioUnitario().multiply(new BigDecimal(detReq.getCantidad()));
            detalle.setSubtotal(subtotal);
            detalle.setVenta(venta); // Enlazar detalle a la venta

            venta.getDetalles().add(detalle);
            totalVenta = totalVenta.add(subtotal); // Sumar al total general
        }

        venta.setTotal(totalVenta);
        String codigo = UUID.randomUUID().toString();
        venta.setCodigoQr(codigo);

        // Guardar en BD
        Venta saved = ventaRepository.save(venta);

        // Marcar asientos como OCUPADO
        if (request.getAsientosIds() != null && !request.getAsientosIds().isEmpty()) {
            String asientosTexto = request.getAsientosIds().stream()
                    .map(id -> {
                        try {
                            String numero = catalogoClient.obtenerAsiento(id)
                                    .map(AsientoDTO::getNumero)
                                    .orElse("A" + id);
                            catalogoClient.ocuparAsiento(id, "\"OCUPADO\"");
                            return numero;
                        } catch (Exception e) {
                            System.err.println("⚠️ Asiento " + id + ": " + e.getMessage());
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
}

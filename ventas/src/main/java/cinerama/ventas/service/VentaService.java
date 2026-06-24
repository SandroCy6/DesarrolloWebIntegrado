package cinerama.ventas.service;

import cinerama.ventas.dto.DetalleRequestDTO;
import cinerama.ventas.dto.VentaRequestDTO;
import cinerama.ventas.model.DetalleVenta;
import cinerama.ventas.model.Venta;
import cinerama.ventas.repository.VentaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VentaService {
    private final VentaRepository ventaRepository;
    private final PagoService pagoService;

    public VentaService(VentaRepository ventaRepository, PagoService pagoService) {
        this.ventaRepository = ventaRepository;
        this.pagoService = pagoService;
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
        venta.setFecha(LocalDateTime.now()); // Fecha automática
        venta.setMetodoPago(request.getMetodoPago()); // Guardamos el metodo de pago
        venta.setEstadoPago("PENDIENTE"); // Estado inicial
        venta.setDetalles(new ArrayList<>());

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
        return ventaRepository.save(venta);
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

}

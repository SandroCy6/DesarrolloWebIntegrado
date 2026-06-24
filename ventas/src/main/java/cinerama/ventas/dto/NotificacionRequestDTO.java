package cinerama.ventas.dto;

import cinerama.ventas.model.DetalleVenta;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class NotificacionRequestDTO {
    private String correo;
    private String telefono;
    private String clienteNombre;
    private String clienteDni;
    private Long ventaId;
    private LocalDateTime fecha;
    private BigDecimal total;
    private List<DetalleItemDTO> detalles;
    private String tituloPelicula;
    private String sala;
    private String codigoQr;
    private String asientos;

    @Data
    public static class DetalleItemDTO {
        private String tipoItem;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
    }

    // Factory method — construye el DTO desde una Venta guardada
    public static NotificacionRequestDTO desde(cinerama.ventas.model.Venta venta) {
        NotificacionRequestDTO dto = new NotificacionRequestDTO();
        dto.setCorreo(venta.getClienteCorreo());
        dto.setClienteDni(venta.getClienteDni());
        dto.setTelefono(venta.getClienteCelular());
        dto.setVentaId(venta.getId());
        dto.setFecha(venta.getFecha());
        dto.setTotal(venta.getTotal());
        dto.setTituloPelicula(venta.getTituloPelicula());
        dto.setSala(venta.getSala());
        dto.setCodigoQr(venta.getCodigoQr());
        dto.setAsientos(venta.getAsientos());

        List<DetalleItemDTO> items = venta.getDetalles().stream().map(d -> {
            DetalleItemDTO item = new DetalleItemDTO();
            item.setTipoItem(d.getTipoItem());
            item.setCantidad(d.getCantidad());
            item.setPrecioUnitario(d.getPrecioUnitario());
            item.setSubtotal(d.getSubtotal());
            return item;
        }).collect(Collectors.toList());

        dto.setDetalles(items);
        return dto;
    }
}
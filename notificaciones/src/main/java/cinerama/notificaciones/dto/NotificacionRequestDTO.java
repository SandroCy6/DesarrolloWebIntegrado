package cinerama.notificaciones.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class NotificacionRequestDTO {
    private String correo;
    private String telefono; // para WhatsApp, ej: "987654321"
    private String clienteNombre;
    private String tituloPelicula;
    private String sala;
    private String asientos;
    private String codigoQr;
    private String clienteDni;
    private Long ventaId;
    private LocalDateTime fecha;
    private BigDecimal total;
    private List<DetalleDTO> detalles;
}
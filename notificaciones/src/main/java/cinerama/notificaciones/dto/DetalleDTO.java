package cinerama.notificaciones.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DetalleDTO {
    private String tipoItem;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
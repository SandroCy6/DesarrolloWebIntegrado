package cinerama.ventas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DetalleRequestDTO {
    @NotBlank
    private String tipoItem; // "ENTRADA" o "SNACK"

    @NotNull
    private Long itemId;

    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    @NotNull
    @Positive(message = "El precio debe ser positivo")
    private BigDecimal precioUnitario;
}
package cinerama.ventas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class VentaRequestDTO {
    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^\\d{8}$", message = "El DNI debe tener exactamente 8 dígitos numéricos")
    private String clienteDni;
    private String clienteCelular;
    private String clienteNombre;
    private List<Long> asientosIds;
    @NotNull(message = "El horarioId es obligatorio")
    private Long horarioId;
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    private String clienteCorreo;

    @NotEmpty(message = "La venta debe tener al menos un detalle")
    @Valid
    private List<DetalleRequestDTO> detalles;
}
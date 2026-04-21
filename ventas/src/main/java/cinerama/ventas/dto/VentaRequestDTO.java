package cinerama.ventas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class VentaRequestDTO {
    @NotBlank(message = "El DNI es obligatorio")
    private String clienteDni;

    @Email(message = "Formato de correo inválido")
    private String clienteCorreo;

    @NotEmpty(message = "La venta debe tener al menos un detalle")
    @Valid
    private List<DetalleRequestDTO> detalles;
}
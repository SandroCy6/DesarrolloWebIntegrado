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

    @Email(message = "Formato de correo inválido")
    private String clienteCorreo;

    @NotBlank(message = "El método de pago es obligatorio (ej. visa, master)")
    private String metodoPago;

    @NotBlank(message = "El token de la tarjeta es obligatorio")
    private String tokenTarjeta;

    @NotEmpty(message = "La venta debe tener al menos un detalle")
    @Valid
    private List<DetalleRequestDTO> detalles;
}
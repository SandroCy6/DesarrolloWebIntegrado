package cinerama.ventas.dto;

import lombok.Data;

@Data
public class AsientoDTO {
    private Long id;
    private String numero;  // "A1", "F12"
    private String estado;
    private Double precio;
}
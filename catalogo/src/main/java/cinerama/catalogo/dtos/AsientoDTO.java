package cinerama.catalogo.dtos;

import cinerama.catalogo.models.EstadoAsiento;
import lombok.Data;

@Data
public class AsientoDTO {
    private Long id;
    private String numero;
    private EstadoAsiento estado;
    private double precio;
    private long SalaId;
    private Long horarioId;
}

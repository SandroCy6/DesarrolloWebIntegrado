package cinerama.ventas.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class HorarioResponseDTO {
    private Long id;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private Double precio;
    private Long peliculaId;
    private String tituloPelicula;
    private Long salaId;
    private int numeroSala;
    private Long cineId;
    private String nombreCine;
}
package cinerama.catalogo.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PeliculaDTO {
    private Long id;
    private Long tmdbId;
    private String titulo;
    private String sinopsis;
    private String genero;
    private Integer duracion;
    private String imagenUrl;
    private LocalDate fechaEstreno;
    private String trailerUrl;
}

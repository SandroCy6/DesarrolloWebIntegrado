package cinerama.catalogo.dtos;

import lombok.Data;

@Data
public class PeliculaDTO {
    private Long id;
    private String titulo;
    private String sinopsis;
    private String genero;
    private Integer duracion;
    private String imagenUrl;
}

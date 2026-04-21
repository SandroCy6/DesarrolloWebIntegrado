package cinerama.catalogo.dtos;

import lombok.Data;

@Data
public class CineDTO {
    private Long id;
    private String nombre;
    private String direccion;
    private String ciudad;
}

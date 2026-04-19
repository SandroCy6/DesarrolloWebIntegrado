package cinerama.catalogo.dtos;

import lombok.Data;

@Data
public class SalaDTO {
    private Long id;
    private int numero;
    private int capacidad;
    private Long cineId; // Solo enviamos el ID para simplificar la respuesta
    private String nombreCine; // Opcional: para mostrarlo fácil en una tabla de Bootstrap
}

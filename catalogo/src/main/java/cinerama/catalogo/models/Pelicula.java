package cinerama.catalogo.models;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "peliculas")
@Data
public class Pelicula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "sinopsis", columnDefinition = "TEXT")
    private String sinopsis;

    @Column(name = "genero", nullable = false)
    private String genero;

    @Column(name = "duracion")
    private int duracion; // Duración en minutos

    @Column(name = "imagen_url")
    private String imagenUrl; // URL de la imagen de la película
    
    
}

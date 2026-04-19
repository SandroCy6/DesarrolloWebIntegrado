package cinerama.catalogo.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cines")
@Data
public class Cine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "direccion", nullable = false)
    private String direccion;

    @Column(name = "ciudad", nullable = false)
    private String ciudad;
}

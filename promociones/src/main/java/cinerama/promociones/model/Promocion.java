package cinerama.promociones.model;

import jakarta.persistence.*;
        import java.time.LocalDate;

@Entity
@Table(name = "promociones")
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_promocion;

    private String titulo;
    private String descripcion;
    private String tipo;
    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;
    private Boolean estado;
    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_regla", referencedColumnName = "id_regla") // Cambia "id_regla" si en tu BD se llama diferente
    private ReglaPromociones regla;

    public Promocion(Long id_promocion, String titulo, String descripcion, String tipo, LocalDate fechaInicio, LocalDate fechaFin, Boolean estado, String imagenUrl, ReglaPromociones regla) {
        this.id_promocion = id_promocion;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.imagenUrl = imagenUrl;
        this.regla = regla;
    }

    public Promocion() {
    }

    public Long getId_promocion() {
        return id_promocion;
    }

    public void setId_promocion(Long id_promocion) {
        this.id_promocion = id_promocion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public ReglaPromociones getRegla() {
        return regla;
    }

    public void setRegla(ReglaPromociones regla) {
        this.regla = regla;
    }
}
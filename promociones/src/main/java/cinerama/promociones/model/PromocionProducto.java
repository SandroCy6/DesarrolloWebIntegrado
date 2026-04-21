package cinerama.promociones.model;

import jakarta.persistence.*;


@Entity
@Table(name = "promocion_producto")
public class PromocionProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_promocion")
    private Long idPromocion;

    @Column(name = "id_producto")
    private Long idProducto;

    private Integer cantidad;

    public PromocionProducto() {}

    public PromocionProducto(Long id, Long idPromocion, Long idProducto, Integer cantidad) {
        this.id = id;
        this.idPromocion = idPromocion;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdPromocion() {
        return idPromocion;
    }

    public void setIdPromocion(Long idPromocion) {
        this.idPromocion = idPromocion;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}


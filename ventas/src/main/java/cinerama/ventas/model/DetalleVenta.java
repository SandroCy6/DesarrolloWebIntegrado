package cinerama.ventas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
public class DetalleVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "venta_id")
    @JsonIgnore // Evita un bucle infinito al devolver el JSON
    private Venta venta;

    private String tipoItem; // Puede ser "ENTRADA" o "SNACK"
    private Long itemId; // El ID de la función (horario) o del producto
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}

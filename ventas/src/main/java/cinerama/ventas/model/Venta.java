package cinerama.ventas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clienteDni;
    private String clienteCorreo;
    private String clienteCelular;
    private String clienteNombre;
    private String tituloPelicula;
    private String sala;
    private String codigoQr;
    private String asientos;
    private BigDecimal total;
    private LocalDateTime fecha;

    private String metodoPago;
    private String estadoPago; // PENDIENTE, APROBADO, RECHAZADO

    // Una venta tiene muchos detalles. Si se guarda la venta, se guardan los detalles
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    private List<DetalleVenta> detalles;

    // Atributos para ingresar datos de las promociones
    @Column(name = "descuento_aplicado")
    private java.math.BigDecimal descuentoAplicado;

    @Column(name = "codigo_promo")
    private String codigoPromo;
}
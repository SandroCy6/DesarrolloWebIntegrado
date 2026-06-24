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

    // Solo se guarda el DNI y correo como referencia al microservicio de Cliente
    private String clienteDni;
    private String clienteCorreo;
    private String clienteCelular;
    private String clienteNombre;
    private String tituloPelicula; 
    private String sala;  
    private String codigoQr; 
    private String asientos; 
    private BigDecimal total;
    private LocalDateTime fecha; // Registro automatico

    // Una venta tiene muchos detalles. Si se guarda la venta, se guardan los
    // detalles
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    private List<DetalleVenta> detalles;
}
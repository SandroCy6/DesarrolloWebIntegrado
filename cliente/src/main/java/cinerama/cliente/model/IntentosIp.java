package cinerama.cliente.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "intentos_ip")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntentosIp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String ip;

    @Builder.Default
    @Column(nullable = false)
    private Integer intentos = 0;

    @Column(name = "bloqueado_hasta")
    private OffsetDateTime bloqueadoHasta;

    @Column(name = "fecha_primer_intento", nullable = false)
    private OffsetDateTime fechaPrimerIntento;

    @Column(name = "fecha_ultimo_intento", nullable = false)
    private OffsetDateTime fechaUltimoIntento;
}

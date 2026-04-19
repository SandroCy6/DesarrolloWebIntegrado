package cinerama.promociones.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "reglas_promocion")
public class ReglaPromociones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_regla")
    private Long idRegla;

    @Column(name = "id_promocion")
    private Long idPromocion;

    @Column(name = "tipo_regla")
    private String tipoRegla;

    @Column(name = "valor_1")
    private BigDecimal valor1;

    @Column(name = "valor_2")
    private BigDecimal valor2;

    public ReglaPromociones() {}

    public ReglaPromociones(Long idRegla, Long idPromocion, String tipoRegla,
                          BigDecimal valor1, BigDecimal valor2) {
        this.idRegla = idRegla;
        this.idPromocion = idPromocion;
        this.tipoRegla = tipoRegla;
        this.valor1 = valor1;
        this.valor2 = valor2;
    }

    public void setIdRegla(Long idRegla) {
        this.idRegla = idRegla;
    }

    public void setIdPromocion(Long idPromocion) {
        this.idPromocion = idPromocion;
    }

    public void setTipoRegla(String tipoRegla) {
        this.tipoRegla = tipoRegla;
    }

    public void setValor1(BigDecimal valor1) {
        this.valor1 = valor1;
    }

    public void setValor2(BigDecimal valor2) {
        this.valor2 = valor2;
    }

    public Long getIdRegla() {
        return idRegla;
    }

    public Long getIdPromocion() {
        return idPromocion;
    }

    public String getTipoRegla() {
        return tipoRegla;
    }

    public BigDecimal getValor1() {
        return valor1;
    }

    public BigDecimal getValor2() {
        return valor2;
    }

}

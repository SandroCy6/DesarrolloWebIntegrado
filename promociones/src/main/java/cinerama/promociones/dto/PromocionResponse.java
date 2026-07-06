package cinerama.promociones.dto;

import java.math.BigDecimal;

public class PromocionResponse {
    private Boolean esValida;
    private String tipoRegla;
    private BigDecimal descuento; // Aquí irá el valor1 (porcentaje o monto fijo)

    public PromocionResponse() {}

    public PromocionResponse(Boolean esValida, String tipoRegla, BigDecimal descuento) {
        this.esValida = esValida;
        this.tipoRegla = tipoRegla;
        this.descuento = descuento;
    }

    // Getters y Setters
    public Boolean getEsValida() { return esValida; }
    public void setEsValida(Boolean esValida) { this.esValida = esValida; }

    public String getTipoRegla() { return tipoRegla; }
    public void setTipoRegla(String tipoRegla) { this.tipoRegla = tipoRegla; }

    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
}

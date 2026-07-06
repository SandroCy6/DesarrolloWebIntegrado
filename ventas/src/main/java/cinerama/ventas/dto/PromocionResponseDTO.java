package cinerama.ventas.dto;

import java.math.BigDecimal;

public class PromocionResponseDTO {
    private Boolean esValida;
    private String tipoRegla;
    private BigDecimal descuento;

    public PromocionResponseDTO() {}

    public Boolean getEsValida() {
        return esValida;
    }
    public void setEsValida(Boolean esValida) { this.esValida = esValida; }

    public String getTipoRegla() { return tipoRegla; }
    public void setTipoRegla(String tipoRegla) { this.tipoRegla = tipoRegla; }

    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
}

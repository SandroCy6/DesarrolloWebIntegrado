package cinerama.promociones.dto;

public class ValidarPromoRequest {
    private String codigo;

    public ValidarPromoRequest() {}

    public ValidarPromoRequest(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}

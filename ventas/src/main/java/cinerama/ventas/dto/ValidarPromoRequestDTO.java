package cinerama.ventas.dto;

public class ValidarPromoRequestDTO {
    private String codigo;

    public ValidarPromoRequestDTO() {}
    public ValidarPromoRequestDTO(String codigo) { this.codigo = codigo; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
}

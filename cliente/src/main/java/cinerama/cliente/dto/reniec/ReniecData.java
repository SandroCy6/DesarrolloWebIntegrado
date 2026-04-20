package cinerama.cliente.dto.reniec;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReniecData {

    private String numero;

    @JsonProperty("nombre_completo")
    private String nombreCompleto;

    private String nombres;

    @JsonProperty("apellido_paterno")
    private String apellidoPaterno;

    @JsonProperty("apellido_materno")
    private String apellidoMaterno;
    
}

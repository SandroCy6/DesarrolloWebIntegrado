package cinerama.cliente.dto.reniec;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReniecResponse {
    private Boolean success;
    private String message;
    private ReniecData data;
}

package cinerama.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UsuarioResponse {

    private Long id;
    private String username;
    private String nombre;
    private String rol;
    private boolean activo;
}
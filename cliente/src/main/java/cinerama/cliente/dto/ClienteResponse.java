package cinerama.cliente.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
public class ClienteResponse {
    
    private Long id;
    private String dni;
    private String nombre;
    private String correo;
    private String telefono;
    private Boolean activo;
    private Boolean dniVerificado;
    private Integer intentosFallidos;
    private OffsetDateTime bloqueadoHasta;
    private OffsetDateTime fechaRegistro;
    private OffsetDateTime fechaActualizacion;
}

package cinerama.cliente.service;

import cinerama.cliente.dto.ClienteRequest;
import cinerama.cliente.dto.ClienteResponse;
import cinerama.cliente.model.Cliente;
import cinerama.cliente.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;

    // TODO: verificar bloqueo por DNI
    // TODO: verificar bloqueo por IP
    // TODO: integrar validacion RENIEC
    @Transactional
    public ClienteResponse verificarORegistrar(ClienteRequest request) {
        clienteRepository.findByDni(request.getDni()).ifPresent(c -> {
            if (!c.getActivo()) {
                throw new RuntimeException("Cliente desactivado. Contacte al administrador. ");
            }
        });
        Cliente cliente = clienteRepository.findByDni(request.getDni())
                .orElse(Cliente.builder()
                        .dni(request.getDni())
                        .activo(true)
                        .dniVerificado(false)
                        .intentosFallidos(0)
                        .build());

        // Siempre actualiza correo y telefono con lo que llega ahora
        cliente.setNombre(request.getNombre());
        cliente.setCorreo(request.getCorreo());
        cliente.setTelefono(request.getTelefono());

        return toResponse(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorDni(String dni) {
        Cliente cliente = clienteRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + dni));
        return toResponse(cliente);
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clienteRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ClienteResponse toResponse(Cliente c) {
        return ClienteResponse.builder()
                .id(c.getId())
                .dni(c.getDni())
                .nombre(c.getNombre())
                .correo(c.getCorreo())
                .telefono(c.getTelefono())
                .activo(c.getActivo())
                .dniVerificado(c.getDniVerificado())
                .intentosFallidos(c.getIntentosFallidos())
                .bloqueadoHasta(c.getBloqueadoHasta())
                .fechaRegistro(c.getFechaRegistro())
                .fechaActualizacion(c.getFechaActualizacion())
                .build();
    }

    @Transactional(readOnly = true)
    public boolean estadoActivo(String dni) {
        return clienteRepository.findByDni(dni)
                .map(Cliente::getActivo)
                .orElse(true);
    }

    private static final int MAX_INTENTOS_CLIENTE = 3;
    private static final int MINUTOS_BLOQUEO_CLIENTE = 15;

    public void registrarFalloDni(String dni) {
        clienteRepository.findByDni(dni).ifPresent(cliente -> {
            int nuevos = cliente.getIntentosFallidos() + 1;
            cliente.setIntentosFallidos(nuevos);

            if (nuevos >= MAX_INTENTOS_CLIENTE) {
                cliente.setBloqueadoHasta(
                        OffsetDateTime.now().plusMinutes(MINUTOS_BLOQUEO_CLIENTE));
            }
            clienteRepository.save(cliente);
        });
    }

    @Transactional(readOnly = true)
    public boolean estaBloqueadoPorDni(String dni) {
        return clienteRepository.findByDni(dni)
                .map(c -> c.getBloqueadoHasta() != null
                        && c.getBloqueadoHasta().isAfter(OffsetDateTime.now()))
                .orElse(false);
    }

}

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
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Propagation;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private static final Logger log = LoggerFactory.getLogger(ClienteService.class);
    private final ClienteRepository clienteRepository;
    private final IntentosIpService intentosIpService;
    private final ReniecService reniecService;
    private final ClienteFalloService clienteFalloService;

    private static final int MAX_INTENTOS_CLIENTE = 3;
    private static final int MINUTOS_BLOQUEO_CLIENTE = 15;

    @Transactional
    public ClienteResponse verificarORegistrar(ClienteRequest request) {
        // Una sola consulta, reutilizada en todas las validaciones
        Optional<Cliente> existente = clienteRepository.findByDni(request.getDni());
        // Validacion 1: cliente desactivado administrativamente
        existente.ifPresent(c -> {
            if (Boolean.FALSE.equals(c.getActivo())) {
                throw new RuntimeException("Cliente desactivado. Contacte al administrador.");
            }
        });
        // Validacion 2: bloqueo temporal por intentos fallidos de DNI
        existente.ifPresent(c -> {
            if (c.getBloqueadoHasta() != null &&
                    c.getBloqueadoHasta().isAfter(OffsetDateTime.now())) {
                throw new RuntimeException("Cliente bloqueado temporalmente. Intente mas tarde.");
            }
        });
        // Validacion 3: verificar con RENIEC
        String nombreReniec = reniecService.obtenerNombreCompleto(request.getDni());

        if (nombreReniec == null) {
            intentosIpService.registrarFalloIp(request.getIp());
            existente.ifPresent(c -> clienteFalloService.registrarFalloDni(request.getDni()));
            throw new RuntimeException("DNI no encontrado en RENIEC. Verifique el numero ingresado.");
        }

        boolean nombreCoincide = reniecService.verificarNombre(nombreReniec, request.getNombre());
        if (!nombreCoincide) {
            intentosIpService.registrarFalloIp(request.getIp());
            clienteFalloService.registrarFalloDni(request.getDni());
            throw new RuntimeException("El nombre no coincide con el registrado en RENIEc para ese DNI.");
        }
        // Todo OK: Obtener o crear cliente
        Cliente cliente = existente.orElse(Cliente.builder()
                .dni(request.getDni())
                .activo(true)
                .dniVerificado(false)
                .intentosFallidos(0)
                .build());

        // Siempre actualiza correo y telefono con lo que llega ahora
        cliente.setNombre(nombreReniec); // Guarda el nombre oficial de RENIEC, no el que escribio el usuario
        cliente.setCorreo(request.getCorreo());
        cliente.setTelefono(request.getTelefono());
        // Reinicia bloqueos y intentos tras verificacion exitosa
        cliente.setIntentosFallidos(0);
        cliente.setBloqueadoHasta(null);
        cliente.setDniVerificado(true);
        intentosIpService.reiniciarIp(request.getIp());
        // Log sin exponer correo/telefono completos
        log.info("[CLIENTE] DNI={} contacto actualizado ip={}",
                request.getDni(),
                request.getIp());

        return toResponse(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorDni(String dni) {
        return clienteRepository.findByDni(dni)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + dni));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clienteRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

  

    @Transactional(readOnly = true)
    public boolean estaBloqueadoPorDni(String dni) {
        return clienteRepository.findByDni(dni)
                .map(c -> c.getBloqueadoHasta() != null
                        && c.getBloqueadoHasta().isAfter(OffsetDateTime.now()))
                .orElse(false);
    }

    // Se llamara desde ReniecService cuando la verificacion sea exitosa
    @Transactional
    public void reiniciarBloqueoDni(String dni) {
        clienteRepository.findByDni(dni).ifPresent(cliente -> {
            cliente.setIntentosFallidos(0);
            cliente.setBloqueadoHasta(null);
            cliente.setDniVerificado(true);
            clienteRepository.save(cliente);
            log.info("[CLIENTE] verificacion exitosa DNI={} dniVerificado=true", dni);
        });
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

}

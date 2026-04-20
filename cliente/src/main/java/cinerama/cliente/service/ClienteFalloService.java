package cinerama.cliente.service;

import cinerama.cliente.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ClienteFalloService {
    private static final Logger log = LoggerFactory.getLogger(ClienteFalloService.class);
    private final ClienteRepository clienteRepository;

    private static final int MAX_INTENTOS_CLIENTE = 3;
    private static final int MINUTOS_BLOQUEO_CLIENTE = 15;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarFalloDni(String dni) {
        clienteRepository.findByDni(dni).ifPresent(cliente -> {
            int nuevos = cliente.getIntentosFallidos() + 1;
            cliente.setIntentosFallidos(nuevos);
            log.warn("[SEGURIDAD] Fallo de verificacion DNI={} intentos={}", dni, nuevos);
            if (nuevos >= MAX_INTENTOS_CLIENTE) {
                cliente.setIntentosFallidos(0);
                cliente.setBloqueadoHasta(
                        OffsetDateTime.now().plusMinutes(MINUTOS_BLOQUEO_CLIENTE));
                log.warn("[SEGURIDAD] Cliente bloqueado DNI={} hasta={}", dni, cliente.getBloqueadoHasta());
            }
            clienteRepository.save(cliente);
        });
    }
}
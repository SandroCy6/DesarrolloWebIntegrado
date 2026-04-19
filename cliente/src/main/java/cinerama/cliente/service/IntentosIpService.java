package cinerama.cliente.service;

import cinerama.cliente.model.IntentosIp;
import cinerama.cliente.repository.IntentosIpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class IntentosIpService {
    private final IntentosIpRepository intentosIpRepository;

    private static final int MAX_INTENTOS_IP = 5;
    private static final int MINUTOS_BLOQUEO_IP = 30;

    @Transactional(readOnly = true)
    public boolean estaIpBloqueada(String ip) {
        return intentosIpRepository.findByIp(ip)
                .map(i -> i.getBloqueadoHasta() != null
                        && i.getBloqueadoHasta().isAfter(OffsetDateTime.now()))
                .orElse(false);
    }

    @Transactional
    public void registrarFalloIp(String ip) {
        IntentosIp registro = intentosIpRepository.findByIp(ip)
                .orElse(IntentosIp.builder()
                        .ip(ip)
                        .intentos(0)
                        .fechaPrimerIntento(OffsetDateTime.now())
                        .fechaUltimoIntento(OffsetDateTime.now())
                        .build());
        int nuevos = registro.getIntentos() + 1;
        registro.setIntentos(nuevos);
        registro.setFechaUltimoIntento(OffsetDateTime.now());

        if (nuevos >= MAX_INTENTOS_IP) {
            registro.setIntentos(0);
            registro.setBloqueadoHasta(
                    OffsetDateTime.now().plusMinutes(MINUTOS_BLOQUEO_IP));
        }
        intentosIpRepository.save(registro);
    }

    @Transactional
    public void reiniciarIp(String ip) {
        intentosIpRepository.findByIp(ip).ifPresent(r -> {
            r.setIntentos(0);
            r.setBloqueadoHasta(null);
            intentosIpRepository.save(r);
        });
    }
}

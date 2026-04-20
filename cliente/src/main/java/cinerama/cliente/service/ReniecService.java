package cinerama.cliente.service;

import cinerama.cliente.dto.reniec.ReniecData;
import cinerama.cliente.dto.reniec.ReniecResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReniecService {
    private static final Logger log = LoggerFactory.getLogger(ReniecService.class);
    private final WebClient.Builder webClientBuilder;

    @Value("${reniec.api.url}")
    private String reniecUrl;

    @Value("${reniec.api.token}")
    private String reniecToken;

    public boolean verificarNombre(String dni, String nombreIngresado) {
        try {
            ReniecResponse respuesta = webClientBuilder.build()
                    .post()
                    .uri(reniecUrl)
                    .header("Authorization", "Bearer " + reniecToken)
                    .header("Content-Type", "application/json")
                    .bodyValue(Map.of("dni", dni))
                    .retrieve()
                    .bodyToMono(ReniecResponse.class)
                    .block();

            if (respuesta == null || !Boolean.TRUE.equals(respuesta.getSuccess())) {
                log.warn("[RENIEC] Sin respuesta valida para DNI={}", dni);
                return false;
            }

            ReniecData data = respuesta.getData();
            String nombreReniec = data.getNombreCompleto().trim().toUpperCase();
            String nombreUsuario = nombreIngresado.trim().toUpperCase();

            boolean coincide = nombreReniec.contains(nombreUsuario) || nombreUsuario.contains(nombreReniec);

            log.info("[RENIEC] DNI={} coincide={}", dni, coincide);
            return coincide;
        } catch (Exception e) {
            log.error("[RENIEC] Error al consultar DNI={} error={}", dni, e.getMessage());
            return false;
        }
    }

    public String obtenerNombreCompleto(String dni) {
        try {
            ReniecResponse respuesta = webClientBuilder.build()
                    .post()
                    .uri(reniecUrl)
                    .header("Authorization", "Bearer " + reniecToken)
                    .header("Content-Type", "application/json")
                    .bodyValue(Map.of("dni", dni))
                    .retrieve()
                    .bodyToMono(ReniecResponse.class)
                    .block();

            if (respuesta == null || !Boolean.TRUE.equals(respuesta.getSuccess())) {
                return null;
            }
            return respuesta.getData().getNombreCompleto();

        } catch (Exception e) {
            log.error("[RENIEC] Error obtenido nombre DNI={} error={}", dni, e.getMessage());
            return null;
        }
    }

}

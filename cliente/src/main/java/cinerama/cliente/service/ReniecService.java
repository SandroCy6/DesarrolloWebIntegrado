package cinerama.cliente.service;

import cinerama.cliente.dto.reniec.ReniecResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.text.Normalizer;

@Service
public class ReniecService {
    private static final Logger log = LoggerFactory.getLogger(ReniecService.class);
    private final RestClient restClient;

    @Value("${reniec.api.url}")
    private String reniecUrl;

    @Value("${reniec.api.token}")
    private String reniecToken;

    public ReniecService() {
        this.restClient = RestClient.create();
    }

    public String obtenerNombreCompleto(String dni) {
        try {
            ReniecResponse respuesta = restClient
                    .post()
                    .uri(reniecUrl)
                    .header("Authorization", "Bearer " + reniecToken)
                    .header("Content-Type", "application/json")
                    .body(Map.of("dni", dni))
                    .retrieve()
                    .body(ReniecResponse.class);

            if (respuesta == null || !Boolean.TRUE.equals(respuesta.getSuccess())) {
                return null;
            }
            return respuesta.getData().getNombreCompleto();

        } catch (Exception e) {
            log.error("[RENIEC] Error obtenido nombre DNI={} error={}", dni, e.getMessage());
            return null;
        }
    }

    public boolean verificarNombre(String nombreReniec, String nombreIngresado) {

        if (nombreReniec == null || nombreIngresado == null)
            return false;

        String r = normalizar(nombreReniec);
        String u = normalizar(nombreIngresado);

        String[] palabras = u.split("\\s+");
        for (String palabra : palabras) {
            if (palabra.length() > 1 && !r.contains(palabra)) {
                log.info("[RENIEC] Palabra '{}' no encontrada en '{}'", palabra, r);
                return false;
            }
        }
        log.info("[RENIEC] Nombre coincide=true");
        return true;
    }

    private String normalizar(String texto) {
        if (texto == null)
            return "";
        return Normalizer
                .normalize(texto.toLowerCase().replaceAll("[,.]", ""), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .trim()
                .replaceAll("\\s+", " ");
    }

}

package cinerama.ventas.client;

import cinerama.ventas.dto.NotificacionRequestDTO;
import cinerama.ventas.model.Venta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class NotificacionClient {

    private final RestTemplate restTemplate;

    public void notificar(Venta venta) {
        try {
            NotificacionRequestDTO dto = NotificacionRequestDTO.desde(venta);
            restTemplate.postForEntity(
                    "http://notificaciones/notificaciones/enviar",
                    dto,
                    String.class);
        } catch (Exception e) {
            // Fallo silencioso: la venta ya fue guardada, no se revierte
            System.err.println("⚠️ No se pudo notificar: " + e.getMessage());
        }
    }
}
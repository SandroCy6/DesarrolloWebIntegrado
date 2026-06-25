package cinerama.notificaciones.controller;

import cinerama.notificaciones.dto.NotificacionRequestDTO;
import cinerama.notificaciones.service.EmailService;
import cinerama.notificaciones.service.WhatsappService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final EmailService emailService;
    private final WhatsappService whatsappService;

    @PostMapping("/enviar")
    public ResponseEntity<String> enviar(@RequestBody NotificacionRequestDTO req) {
        try {
            // Correo SIEMPRE
            emailService.enviarConfirmacion(req);

            // WhatsApp solo si tiene celular
            if (req.getTelefono() != null && !req.getTelefono().isBlank()) {
                whatsappService.enviarConfirmacion(
                        req.getTelefono(),
                        req.getVentaId(),
                        req.getTotal(),
                        req.getTituloPelicula(),
                        req.getSala(),
                        req.getCodigoQr());
            }

            return ResponseEntity.ok("Notificación enviada.");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Error: " + e.getMessage());
        }
    }
}
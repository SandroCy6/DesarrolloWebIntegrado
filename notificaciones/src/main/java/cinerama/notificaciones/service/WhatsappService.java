package cinerama.notificaciones.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WhatsappService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.from}")
    private String from;

    public void enviarConfirmacion(String telefono, Long ventaId, BigDecimal total,
            String tituloPelicula, String sala, String codigoQr) {
        Twilio.init(accountSid, authToken);
        try {
            String mensaje = String.format(
                    "🎬 *CINERAMA — Compra Confirmada* ✅\n\n" +
                            "📋 Venta #%d\n" +
                            "🎥 Película: %s\n" +
                            "📍 %s\n" +
                            "💰 Total: S/ %s\n\n" +
                            "🔑 Tu código QR: *%s*\n\n" +
                            "📧 Te enviamos la boleta completa a tu correo.\n" +
                            "Si no aparece, revisa tu carpeta de *Spam o Correo no deseado*.\n\n" +
                            "¡Disfruta la película! 🍿",
                    ventaId,
                    tituloPelicula != null ? tituloPelicula : "—",
                    sala != null ? sala : "—",
                    total,
                    codigoQr != null ? codigoQr : "—");

            Message.creator(
                    new PhoneNumber("whatsapp:+51" + telefono),
                    new PhoneNumber(from),
                    mensaje).create();

        } catch (Exception e) {
            System.err.println("⚠️ WhatsApp no enviado: " + e.getMessage());
        }
    }
}
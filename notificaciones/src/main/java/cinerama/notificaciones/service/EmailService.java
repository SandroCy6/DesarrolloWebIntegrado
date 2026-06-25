package cinerama.notificaciones.service;

import cinerama.notificaciones.dto.DetalleDTO;
import cinerama.notificaciones.dto.NotificacionRequestDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;
  private final PdfService pdfService;

  public void enviarConfirmacion(NotificacionRequestDTO req) throws MessagingException {
    MimeMessage msg = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
    helper.setFrom("noreply@73829104.xyz");
    helper.setTo(req.getCorreo());
    helper.setSubject(" Confirmación de compra Cinerama #" + req.getVentaId());
    helper.setText(buildHtml(req), true);

    // PDF adjunto
    byte[] pdf = pdfService.generarResumen(req);
    helper.addAttachment(
        "Cinerama_Compra_" + req.getVentaId() + ".pdf",
        new ByteArrayResource(pdf));

    mailSender.send(msg);
  }

  private String buildHtml(NotificacionRequestDTO req) {
    StringBuilder filas = new StringBuilder();
    for (DetalleDTO d : req.getDetalles()) {
      filas.append("""
          <tr>
            <td style="padding:8px;border-bottom:1px solid #2a2a2a">%s</td>
            <td style="padding:8px;border-bottom:1px solid #2a2a2a;text-align:center">%d</td>
            <td style="padding:8px;border-bottom:1px solid #2a2a2a;text-align:right">S/ %s</td>
            <td style="padding:8px;border-bottom:1px solid #2a2a2a;text-align:right">S/ %s</td>
          </tr>
          """.formatted(
          d.getTipoItem(),
          d.getCantidad(),
          d.getPrecioUnitario(),
          d.getSubtotal()));
    }

    return """
        <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto;background:#111111;color:#F5F5F5;border-radius:10px;overflow:hidden">

          <!-- Header -->
          <div style="background:#E8001C;padding:28px;text-align:center">
            <h1 style="margin:0;color:white;letter-spacing:4px;font-size:28px">CINERAMA</h1>
            <p style="margin:6px 0 0;color:#ffffffcc;font-size:14px">Confirmación de compra</p>
          </div>

          <!-- Body -->
          <div style="padding:28px">
            <p style="font-size:16px">Hola <strong>%s</strong>, tu pago fue
              <span style="color:#E8001C;font-weight:bold">APROBADO ✅</span>
            </p>
            <p style="color:#aaa;font-size:13px">Venta #%d &nbsp;|&nbsp; %s</p>

            <!-- Tabla detalles -->
            <table width="100%%" style="border-collapse:collapse;margin-top:16px;font-size:14px">
              <thead>
                <tr style="background:#E8001C;color:white">
                  <th style="padding:10px;text-align:left">Item</th>
                  <th style="padding:10px">Cant.</th>
                  <th style="padding:10px;text-align:right">P. Unit.</th>
                  <th style="padding:10px;text-align:right">Subtotal</th>
                </tr>
              </thead>
              <tbody>
                %s
              </tbody>
            </table>

            <!-- Total -->
            <div style="text-align:right;margin-top:16px;font-size:18px">
              <strong style="color:#E8001C">TOTAL: S/ %s</strong>
            </div>

            <hr style="border:none;border-top:1px solid #2a2a2a;margin:24px 0">

            <p style="color:#888;font-size:12px;text-align:center">
              Presenta este correo o el PDF adjunto en taquilla.<br>
              ¡Disfruta la película! 🎬
            </p>
          </div>

          <!-- Footer -->
          <div style="background:#1a1a1a;padding:14px;text-align:center">
            <p style="margin:0;color:#555;font-size:11px">© 2026 Cinerama — Acceso restringido al personal autorizado</p>
          </div>

        </div>
        """
        .formatted(
            req.getClienteNombre() != null ? req.getClienteNombre() : "Cliente",
            req.getVentaId(),
            req.getFecha(),
            filas.toString(),
            req.getTotal());
  }
}
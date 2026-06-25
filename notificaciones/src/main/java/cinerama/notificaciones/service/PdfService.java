package cinerama.notificaciones.service;

import cinerama.notificaciones.dto.DetalleDTO;
import cinerama.notificaciones.dto.NotificacionRequestDTO;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.HorizontalAlignment;
import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

        private static final DeviceRgb ROJO_CINERAMA = new DeviceRgb(232, 0, 28);
        private static final DeviceRgb GRIS_OSCURO = new DeviceRgb(30, 30, 30);

        public byte[] generarResumen(NotificacionRequestDTO req) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                PdfWriter writer = new PdfWriter(baos);
                PdfDocument pdf = new PdfDocument(writer);
                Document doc = new Document(pdf);

                // Encabezado

                doc.add(new Paragraph("CINERAMA")
                                .setFontSize(26).setBold()
                                .setFontColor(ROJO_CINERAMA)
                                .setTextAlignment(TextAlignment.CENTER));

                doc.add(new Paragraph("Comprobante de Compra")
                                .setFontSize(12)
                                .setFontColor(ColorConstants.GRAY)
                                .setTextAlignment(TextAlignment.CENTER));
                if (req.getTituloPelicula() != null) {
                        doc.add(new Paragraph("🎬 " + req.getTituloPelicula())
                                        .setFontSize(13).setBold().setFontColor(new DeviceRgb(232, 0, 28)));
                        doc.add(new Paragraph("📍 " + req.getSala()).setFontSize(10));
                }
                if (req.getAsientos() != null) {
                        doc.add(new Paragraph("💺 Asientos: " + req.getAsientos())
                                        .setFontSize(10));
                }
                // QR
                if (req.getCodigoQr() != null) {
                        byte[] qrBytes = generarQrBytes(req.getCodigoQr());
                        Image qr = new Image(ImageDataFactory.create(qrBytes));
                        qr.setWidth(100).setHorizontalAlignment(HorizontalAlignment.CENTER);
                        doc.add(qr);
                        doc.add(new Paragraph("Código: " + req.getCodigoQr())
                                        .setFontSize(8).setTextAlignment(TextAlignment.CENTER)
                                        .setFontColor(ColorConstants.GRAY));
                }
                doc.add(new Paragraph(" "));

                // Datos de la venta
                doc.add(new Paragraph("Venta #" + req.getVentaId())
                                .setFontSize(11).setBold());
                doc.add(new Paragraph("Fecha: " + req.getFecha())
                                .setFontSize(10));
                doc.add(new Paragraph("Cliente DNI: " + req.getClienteDni())
                                .setFontSize(10));
                doc.add(new Paragraph(" "));

                // Tabla de detalles
                Table tabla = new Table(UnitValue.createPercentArray(new float[] { 4, 1, 2, 2 }))
                                .setWidth(UnitValue.createPercentValue(100));

                // Cabecera tabla
                for (String header : new String[] { "Item", "Cant.", "P. Unit.", "Subtotal" }) {
                        tabla.addHeaderCell(new Cell()
                                        .add(new Paragraph(header).setBold().setFontColor(ColorConstants.WHITE))
                                        .setBackgroundColor(ROJO_CINERAMA));
                }

                // Filas
                for (DetalleDTO d : req.getDetalles()) {
                        tabla.addCell(d.getTipoItem());
                        tabla.addCell(String.valueOf(d.getCantidad()));
                        tabla.addCell("S/ " + d.getPrecioUnitario());
                        tabla.addCell("S/ " + d.getSubtotal());
                }

                doc.add(tabla);
                doc.add(new Paragraph(" "));

                // Total
                doc.add(new Paragraph("TOTAL: S/ " + req.getTotal())
                                .setFontSize(14).setBold()
                                .setFontColor(ROJO_CINERAMA)
                                .setTextAlignment(TextAlignment.RIGHT));

                doc.add(new Paragraph("Presenta este documento en taquilla. ¡Disfruta la película!")
                                .setFontSize(9)
                                .setFontColor(ColorConstants.GRAY)
                                .setTextAlignment(TextAlignment.CENTER));

                doc.close();
                return baos.toByteArray();
        }

        private byte[] generarQrBytes(String contenido) {
                try {
                        QRCodeWriter writer = new QRCodeWriter();
                        BitMatrix matrix = writer.encode(contenido, BarcodeFormat.QR_CODE, 200, 200);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        MatrixToImageWriter.writeToStream(matrix, "PNG", out);
                        return out.toByteArray();
                } catch (Exception e) {
                        throw new RuntimeException("Error generando QR: " + e.getMessage());
                }
        }

}
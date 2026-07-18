package cinerama.ventas.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PagoService {
    private final String ACCESS_TOKEN = "TEST-4996197481824528-062402-963b4c7e100b898383dca862280d16da-3492632721";

    public boolean procesarPago(BigDecimal monto, String metodoPago, String tokenTarjeta, String emailCliente) {
        // 🛡️ BYPASS PARA PRUEBAS EN POSTMAN: Si mandamos "tok_test", forzamos la aprobación
        //if ("tok_test".equals(tokenTarjeta)) return true;

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.mercadopago.com/v1/payments";

        // 1. Configurar Cabeceras
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(ACCESS_TOKEN);

        // Cabecera para evitar cobros duplicados por accidente
        headers.set("X-Idempotency-Key", UUID.randomUUID().toString());

        // 2. Armar el cuerpo de la petición (JSON)
        Map<String, Object> body = new HashMap<>();
        body.put("transaction_amount", monto.doubleValue());
        body.put("description", "Compra de entradas - Cinerama");
        body.put("payment_method_id", metodoPago.toLowerCase()); // ejemplo: "visa"
        body.put("token", tokenTarjeta); // El token de la tarjeta de prueba
        body.put("installments", 1); // Cuotas requeridas por defecto

        // Información obligatoria del pagador
        Map<String, String> payer = new HashMap<>();
        payer.put("email", emailCliente);
        body.put("payer", payer);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            // 3. Realizar la llamada real
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            // Si Mercado Pago responde 201 Created o 200 OK
            if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
                String status = (String) response.getBody().get("status");
                return "approved".equals(status);
            }
            return false;

        } catch (org.springframework.web.client.RestClientResponseException e) {
            System.err.println("El pago fue rechazado o falló en MercadoPago: " + e.getResponseBodyAsString());
            return false;
        } catch (Exception e) {
            System.err.println("Error interno en la comunicación con Mercado Pago: " + e.getMessage());
            return false;
        }
    }


}

package cinerama.ventas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PagoService {

    @Value("${app.pagos.simulado:false}")
    private boolean pagoSimulado;

    @Value("${mercadopago.access-token:TEST-4996197481824528-062402-963b4c7e100b898383dca862280d16da-3492632721}")
    private String accessToken;

    public boolean procesarPago(BigDecimal monto, String metodoPago, String tokenTarjeta, String emailCliente) {

        if (pagoSimulado) {
            System.out.println("⚠️ Pago simulado en entorno de desarrollo");
            return true;
        }

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.mercadopago.com/v1/payments";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        headers.set("X-Idempotency-Key", UUID.randomUUID().toString());

        Map<String, Object> body = new HashMap<>();
        body.put("transaction_amount", monto.doubleValue());
        body.put("description", "Compra de entradas - Cinerama");
        body.put("payment_method_id", metodoPago.toLowerCase());
        body.put("token", tokenTarjeta);
        body.put("installments", 1);

        Map<String, String> payer = new HashMap<>();
        payer.put("email", emailCliente);
        body.put("payer", payer);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
                Map responseBody = response.getBody();
                String status = responseBody != null ? (String) responseBody.get("status") : null;

                System.out.println("✅ MP RESPONSE OK: " + responseBody);

                return "approved".equalsIgnoreCase(status);
            }

            System.err.println("⚠️ Mercado Pago respondió con estado HTTP no esperado: " + response.getStatusCode());
            return false;

        } catch (RestClientResponseException e) {
            System.err.println("🔴 MP RESPONSE ERROR:");
            System.err.println(e.getResponseBodyAsString());
            return false;
        } catch (Exception e) {
            System.err.println("🔴 Error interno en la comunicación con Mercado Pago: " + e.getMessage());
            return false;
        }
    }

    public boolean isPagoSimulado() {
        return pagoSimulado;
    }
}
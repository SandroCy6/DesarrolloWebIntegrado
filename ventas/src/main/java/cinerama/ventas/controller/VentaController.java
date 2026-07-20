package cinerama.ventas.controller;

import cinerama.ventas.dto.VentaRequestDTO;
import cinerama.ventas.model.Venta;
import cinerama.ventas.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    public ResponseEntity<?> registrarVenta(@Valid @RequestBody VentaRequestDTO request) {
        try {
            Venta nuevaVenta = ventaService.registrarVenta(request);

            Map<String, Object> response = new HashMap<>();
            response.put("ok", true);
            response.put("venta", nuevaVenta);

            if ("APROBADO_SIMULADO".equalsIgnoreCase(nuevaVenta.getEstadoPago())) {
                response.put("pagoSimulado", true);
                response.put("mensaje",
                        "Pago procesado en modo simulación para fines académicos. No se realizó ningún cobro real.");
            } else {
                response.put("pagoSimulado", false);
                response.put("mensaje", "Pago aprobado correctamente.");
            }

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("ok", false);
            error.put("pagoSimulado", false);
            error.put("mensaje", e.getMessage());

            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> consultarVenta(@PathVariable Long id) {
        try {
            Venta venta = ventaService.obtenerVentaPorId(id);

            Map<String, Object> response = new HashMap<>();
            response.put("ok", true);
            response.put("venta", venta);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("ok", false);
            error.put("mensaje", e.getMessage());

            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/cliente/{dni}")
    public ResponseEntity<?> consultarHistorialCliente(@PathVariable String dni) {
        List<Venta> historial = ventaService.obtenerHistorialPorDni(dni);

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("historial", historial);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> listarTodasLasVentas(
            @PageableDefault(size = 100, sort = "fecha", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Venta> ventasPaginadas = ventaService.obtenerTodasLasVentas(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("ventas", ventasPaginadas);

        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarErroresValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String nombreCampo = ((FieldError) error).getField();
            String mensaje = error.getDefaultMessage();
            errores.put(nombreCampo, mensaje);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("ok", false);
        response.put("mensaje", "Errores de validación en la solicitud.");
        response.put("errores", errores);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> manejarErrorDeFormatoJSON(
            org.springframework.http.converter.HttpMessageNotReadableException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("ok", false);
        response.put("mensaje",
                "Formato de dato incorrecto. Verifica que las cantidades sean números enteros (sin decimales) y que los tipos de datos sean correctos.");

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarErrorGeneral(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("ok", false);
        response.put("mensaje", "Ocurrió un error interno en el servidor.");
        response.put("detalle", ex.getMessage());

        return ResponseEntity.internalServerError().body(response);
    }
}
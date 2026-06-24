package cinerama.ventas.controller;

import cinerama.ventas.dto.VentaRequestDTO;
import cinerama.ventas.model.Venta;
import cinerama.ventas.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
            return ResponseEntity.ok(nuevaVenta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Consultar estado de una venta específica
    @GetMapping("/{id}")
    public ResponseEntity<?> consultarVenta(@PathVariable Long id) {
        try {
            Venta venta = ventaService.obtenerVentaPorId(id);
            return ResponseEntity.ok(venta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Historial de compras de un cliente
    @GetMapping("/cliente/{dni}")
    public ResponseEntity<?> consultarHistorialCliente(@PathVariable String dni) {
        return ResponseEntity.ok(ventaService.obtenerHistorialPorDni(dni));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarErroresValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String nombreCampo = ((FieldError) error).getField();
            String mensaje = error.getDefaultMessage();
            errores.put(nombreCampo, mensaje);
        });

        return ResponseEntity.badRequest().body(errores);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> manejarErrorDeFormatoJSON(org.springframework.http.converter.HttpMessageNotReadableException ex) {
        Map<String, String> error = new HashMap<>();

        error.put("error", "Formato de dato incorrecto. Verifica que las cantidades sean números enteros (sin decimales) y que los tipos de datos sean correctos.");

        return ResponseEntity.badRequest().body(error);
    }
}

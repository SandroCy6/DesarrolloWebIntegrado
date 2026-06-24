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
            return ResponseEntity.ok(nuevaVenta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Consultar detalle de una venta específica
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
        List<Venta> historial = ventaService.obtenerHistorialPorDni(dni);
        return ResponseEntity.ok(historial);
    }

    // Listar todas las ventas (Solo ADMIN con Paginación)
    @GetMapping
    public ResponseEntity<?> listarTodasLasVentas(
            @PageableDefault(size = 10, sort = "fecha", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        // Control extra de seguridad por cabecera si el Api Gateway inyecta el Rol decodificado del JWT
        if (role == null || !role.equalsIgnoreCase("ADMIN")) {
            return ResponseEntity.status(403).body("Acceso denegado: Se requieren permisos de Administrador");
        }

        Page<Venta> ventasPaginadas = ventaService.obtenerTodasLasVentas(pageable);
        return ResponseEntity.ok(ventasPaginadas);
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

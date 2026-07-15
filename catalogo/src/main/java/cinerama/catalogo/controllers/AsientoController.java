package cinerama.catalogo.controllers;

import cinerama.catalogo.dtos.AsientoDTO;
import cinerama.catalogo.models.EstadoAsiento;
import cinerama.catalogo.services.AsientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cines")
public class AsientoController {

    @Autowired
    private AsientoService asientoService;

    // GET /api/cines/{salaId}/asientos
    @GetMapping("/{salaId}/asientos")
    public List<AsientoDTO> listarAsientosPorSala(@PathVariable Long salaId) {
        return asientoService.listarPorSala(salaId);
    }

    // NUEVA RUTA PARA ANGULAR: Asientos específicos de una función
    @GetMapping("/horarios/{horarioId}/asientos")
    public List<AsientoDTO> listarAsientosPorHorario(@PathVariable Long horarioId) {
        return asientoService.listarPorHorario(horarioId);
    }

    // GET /api/cines/asientos/{asientoId}
    @GetMapping("/asientos/{asientoId}")
    public ResponseEntity<AsientoDTO> obtenerDetalleAsiento(@PathVariable Long asientoId) {
        return asientoService.obtenerDetalle(asientoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/cines/asientos/{asientoId}/estado
    @PutMapping("/asientos/{asientoId}/estado")
    public ResponseEntity<?> actualizarEstadoAsiento(
            @PathVariable Long asientoId,
            @RequestBody String estadoStr) {
        try {
            // Limpiamos el texto por si mandan JSON con comillas (ej: "OCUPADO")
            String estadoLimpio = estadoStr.replace("\"", "").trim().toUpperCase();
            EstadoAsiento estadoEnum = EstadoAsiento.valueOf(estadoLimpio);

            AsientoDTO asientoActualizado = asientoService.actualizarEstado(asientoId, estadoEnum);
            return ResponseEntity.ok(asientoActualizado);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: El estado debe ser LIBRE, OCUPADO o MANTENIMIENTO");
        } catch (IllegalStateException e) {
            // 409 Conflict: El asiento ya fue tomado por otra persona
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/cines/{salaId}/asientos
    @PostMapping("/horarios/{horarioId}/asientos")
    public ResponseEntity<AsientoDTO> crearAsiento(
            @PathVariable Long horarioId,
            @RequestBody AsientoDTO dto) {
        return ResponseEntity.ok(asientoService.crearAsiento(horarioId, dto));
    }
}
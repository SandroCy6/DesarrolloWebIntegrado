package cinerama.promociones.controller;

import cinerama.promociones.dto.PromocionResponse;
import cinerama.promociones.dto.ValidarPromoRequest;
import cinerama.promociones.service.PromocionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import cinerama.promociones.model.Promocion;

@RestController
@RequestMapping("/promociones")
public class PromocionController {

    @Autowired
    private PromocionService promocionService;

    // LISTAR
    @GetMapping
    public List<Promocion> listar() {
        return promocionService.listar();
    }


    // CREAR
    @PostMapping
    public Promocion guardar(@RequestBody Promocion obj) {
        return promocionService.guardar(obj);
    }

    // ACTUALIZAR
    @PutMapping("/{id}")
    public Promocion actualizar(@PathVariable Long id, @RequestBody Promocion obj) {
        return promocionService.actualizar(id, obj);
    }

    // ELIMINAR
    @DeleteMapping("/{id}")
    public String eliminar(@PathVariable Long id) {
        promocionService.eliminar(id);
        return "Promoción eliminada";
    }

    // Tarea Parte 1: OBTENER /promociones/activas — listar promociones vigentes
    @GetMapping("/activas")
    public ResponseEntity<List<Promocion>> obtenerPromocionesActivas() {
        List<Promocion> activas = promocionService.listarPromocionesActivas();
        return ResponseEntity.ok(activas);
    }

    // Tarea Parte 4: Endpoint POST /promociones/validar → recibe código promo y devuelve descuento
    @PostMapping("/validar")
    public ResponseEntity<PromocionResponse> validarPromocion(@RequestBody ValidarPromoRequest request) {
        PromocionResponse response = promocionService.validarPromocion(request);
        return ResponseEntity.ok(response);
    }
}
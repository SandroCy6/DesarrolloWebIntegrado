package cinerama.promociones.controller;

import cinerama.promociones.service.PromocionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import cinerama.promociones.model.Promocion;

@RestController
@RequestMapping("/promociones")
public class PromocionController {

    @Autowired
    private PromocionService service;

    // LISTAR
    @GetMapping
    public List<Promocion> listar() {
        return service.listar();
    }


    // CREAR
    @PostMapping
    public Promocion guardar(@RequestBody Promocion obj) {
        return service.guardar(obj);
    }

    // ACTUALIZAR
    @PutMapping("/{id}")
    public Promocion actualizar(@PathVariable Long id, @RequestBody Promocion obj) {
        return service.actualizar(id, obj);
    }

    // ELIMINAR
    @DeleteMapping("/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Promoción eliminada";
    }
}
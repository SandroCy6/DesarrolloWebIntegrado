package cinerama.promociones.controller;

import cinerama.promociones.model.PromocionProducto;
import cinerama.promociones.service.PromocionProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/promocion-producto")
public class PromocionProductoController {

    @Autowired
    private PromocionProductoService service;

    // LISTAR
    @GetMapping
    public List<PromocionProducto> listar() {
        return service.listar();
    }

    // CREAR
    @PostMapping
    public PromocionProducto guardar(@RequestBody PromocionProducto obj) {
        return service.guardar(obj);
    }

    // ACTUALIZAR
    @PutMapping("/{id}")
    public PromocionProducto actualizar(@PathVariable Long id, @RequestBody PromocionProducto obj) {
        return service.actualizar(id, obj);
    }

    // ELIMINAR
    @DeleteMapping("/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
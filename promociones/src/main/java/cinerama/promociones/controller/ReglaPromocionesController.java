package cinerama.promociones.controller;

import cinerama.promociones.model.ReglaPromociones;
import cinerama.promociones.service.ReglaPromocionesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reglas")
public class ReglaPromocionesController {

    @Autowired
    private ReglaPromocionesService service;

    // 🔹 LISTAR TODAS
    @GetMapping
    public List<ReglaPromociones> listar() {
        return service.listar();
    }

    // 🔹 BUSCAR POR ID
    @GetMapping("/{id}")
    public ReglaPromociones obtener(@PathVariable Long id) {
        return service.buscarPorId(id);
    }


    // 🔹 CREAR
    @PostMapping
    public ReglaPromociones guardar(@RequestBody ReglaPromociones obj) {
        return service.guardar(obj);
    }

    // 🔹 ACTUALIZAR
    @PutMapping("/{id}")
    public ReglaPromociones actualizar(@PathVariable Long id, @RequestBody ReglaPromociones obj) {
        return service.actualizar(id, obj);
    }

    // 🔹 ELIMINAR
    @DeleteMapping("/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Regla eliminada correctamente";
    }
}
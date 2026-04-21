package cinerama.catalogo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cinerama.catalogo.dtos.CineDTO;
import cinerama.catalogo.models.Cine;
import cinerama.catalogo.services.CineService;



@RestController
@RequestMapping("/api/cines")
@CrossOrigin(origins = "*") // Permite que Angular se conecte sin errores de CORS
public class CineController {

    @Autowired
    private CineService cineService;

    @GetMapping
    public List<CineDTO> listarCines(){
        return cineService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CineDTO> obtenerCine(@PathVariable Long id){
        return cineService.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping()
    public ResponseEntity<CineDTO> CrearCine(@RequestBody Cine cine) {
        CineDTO nuevoCine = cineService.guardar(cine);
        return new ResponseEntity<>(nuevoCine, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> EliminarCine(@PathVariable Long id){
        cineService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    

}

package cinerama.catalogo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import cinerama.catalogo.dtos.SalaDTO;
import cinerama.catalogo.models.Sala;
import cinerama.catalogo.services.SalaService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/catalogo/salas")
public class SalaController {

    @Autowired
    private SalaService salaService;

    @GetMapping()
    public List<SalaDTO> listarSalas(){
        return salaService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalaDTO> obtenerSala(@PathVariable Long id){
        return salaService.obtenerPorId(id)
            .map(ResponseEntity::ok).orElse(ResponseEntity
                .notFound().build());
    }

    @GetMapping("/cine/{cineId}")
    public List<SalaDTO> listarSalasPorCine(@PathVariable Long cineId){
        return salaService.obtenerPorCine(cineId);
    }

    @PostMapping
    public ResponseEntity<SalaDTO> crearSala(@RequestBody Sala sala){
        SalaDTO nuevaSala = salaService.guardar(sala);
        return new ResponseEntity<>(nuevaSala,HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSala(@PathVariable Long id){
        salaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    
}

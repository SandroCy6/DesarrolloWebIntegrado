package cinerama.catalogo.controllers;

import cinerama.catalogo.dtos.HorarioDTO;
import cinerama.catalogo.models.Horario;
import cinerama.catalogo.repositories.HorarioRepository;
import cinerama.catalogo.services.HorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/catalogo/horarios")
public class HorarioController {

    @Autowired
    private HorarioService horarioService;

    @GetMapping
    public List<HorarioDTO> listarHorarios(){
        return horarioService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioDTO> obtenerHorario(@PathVariable Long id){
        return horarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pelicula/{peliculaId}/fecha/{fecha}")
    public List<HorarioDTO> listarPorPeliculaYFecha(@PathVariable Long peliculaId,
    @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha){
        return horarioService.obtenerPorPeliculaYFecha(peliculaId,fecha);
    }

    @PostMapping
    public ResponseEntity<HorarioDTO> crearHorario(@RequestBody Horario horario){
        HorarioDTO nuevoHorario = horarioService.guardar(horario);
        return new  ResponseEntity<>(nuevoHorario, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarHorario(@PathVariable Long id){
        horarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<HorarioDTO> actualizarHorario(@PathVariable Long id, @RequestBody Horario horarioActualizado) {
        return ResponseEntity.ok(horarioService.actualizar(id, horarioActualizado));
    }

}

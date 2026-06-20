package cinerama.catalogo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cinerama.catalogo.dtos.PeliculaDTO;
import cinerama.catalogo.models.Pelicula;
import cinerama.catalogo.services.PeliculaService;

@RestController
@RequestMapping("/catalogo/peliculas")
@CrossOrigin(origins = "*")
public class PeliculaController {
    
    @Autowired
    private PeliculaService peliculaService;

    @GetMapping
    public List<PeliculaDTO> listarPeliculas(@RequestParam(required = false) Long cineId) {
        if (cineId != null) {
            return peliculaService.obtenerPorCine(cineId);
        }
        return peliculaService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeliculaDTO> obtenerPelicula(@PathVariable Long id){
        return peliculaService.obtenerPorId(id)
            .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public List<PeliculaDTO> buscarPorTitulo(@RequestParam String titulo){
        return peliculaService.buscarPorTitulo(titulo);
    }

    @GetMapping("/genero/{genero}")
    public List<PeliculaDTO> filtrarPorGenero(@PathVariable String genero){
        return peliculaService.buscarPorGenero(genero);
    }

    @PostMapping
    public ResponseEntity<PeliculaDTO> crearPelicula(@RequestBody Pelicula pelicula){
        PeliculaDTO nuevaPelicula = peliculaService.guardar(pelicula);
        return new ResponseEntity<>(nuevaPelicula, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPelicula(@PathVariable Long id){
        peliculaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tmdb/buscar")
    public ResponseEntity<String> buscarPeliculasTMDB(@RequestParam String query){
        try{
            String jsonRespuesta = peliculaService.buscarEnTMDB(query);
            return ResponseEntity.ok(jsonRespuesta);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al buscar en TMDB "+e.getMessage());
        }
    }

    @PostMapping("tmdb/importar/{tmdbId}")
    public ResponseEntity<?> importarPeliculasTMDB(@PathVariable Long tmdbId){
        try{
            PeliculaDTO peliculaImportada = peliculaService.importarDesdeTMDB(tmdbId);
            return new  ResponseEntity<>(peliculaImportada, HttpStatus.CREATED);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar en TMDB "+e.getMessage());
        }
    }

    @GetMapping("/proximos-estrenos")
    public List<PeliculaDTO> listarProximosEstrenos() {
        return peliculaService.obtenerProximosEstrenos();
    }

}

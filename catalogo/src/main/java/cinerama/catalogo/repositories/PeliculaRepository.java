package cinerama.catalogo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import cinerama.catalogo.models.Pelicula;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {

    @Query("SELECT DISTINCT p FROM Pelicula p INNER JOIN Horario h ON p.id = h.pelicula.id WHERE h.fecha >= CURRENT_DATE AND p.fechaEstreno <= CURRENT_DATE")
    List<Pelicula> findPeliculasEnCartelera();

    List<Pelicula> findByGenero(String genero);
     List<Pelicula> findByTituloContainingIgnoreCase(String titulo);
    List<Pelicula> findByFechaEstrenoAfter(LocalDate fechaActual);
    // Busca las películas que tienen horarios programados en las salas de un cine específico
    @Query("SELECT DISTINCT h.pelicula FROM Horario h WHERE h.sala.cine.id = :cineId")
    List<Pelicula> findPeliculasByCineId(@Param("cineId") Long cineId);
}

package cinerama.catalogo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cinerama.catalogo.models.Pelicula;
import java.util.List;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {

    List<Pelicula> findByGenero(String genero);
     List<Pelicula> findByTituloContainingIgnoreCase(String titulo);
}

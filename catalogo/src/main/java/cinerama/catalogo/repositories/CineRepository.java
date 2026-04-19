package cinerama.catalogo.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cinerama.catalogo.models.Cine;

@Repository
public interface CineRepository extends JpaRepository<Cine,Long> {
    List<Cine> findByCiudad(String ciudad);
}

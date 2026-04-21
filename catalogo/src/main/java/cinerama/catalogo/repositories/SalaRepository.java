package cinerama.catalogo.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cinerama.catalogo.models.Sala;

@Repository
public interface SalaRepository extends JpaRepository<Sala,Long>{
    List<Sala> findByCineId(Long cineId);
}

package cinerama.promociones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cinerama.promociones.model.Promocion;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    List<Promocion> findByEstadoTrueAndFechaFinGreaterThanEqual(LocalDate fecha);

    Optional<Promocion> findByTituloAndEstadoTrue(String titulo);
}
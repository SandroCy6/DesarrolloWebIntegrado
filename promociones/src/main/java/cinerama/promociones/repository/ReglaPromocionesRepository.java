package cinerama.promociones.repository;

import cinerama.promociones.model.ReglaPromociones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReglaPromocionesRepository extends JpaRepository<ReglaPromociones, Long> {

    // Buscar la regla usando el ID de la promoción
    Optional<ReglaPromociones> findByIdPromocion(Long idPromocion);
}
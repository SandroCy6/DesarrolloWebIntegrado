package cinerama.promociones.repository;

import cinerama.promociones.model.ReglaPromociones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReglaPromocionesRepository extends JpaRepository<ReglaPromociones, Long> {
    List<ReglaPromociones> findByIdPromocion(Long idPromocion);

}
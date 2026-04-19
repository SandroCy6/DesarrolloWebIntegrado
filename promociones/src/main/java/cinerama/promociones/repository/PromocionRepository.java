package cinerama.promociones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cinerama.promociones.model.Promocion;
import org.springframework.stereotype.Repository;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {

}
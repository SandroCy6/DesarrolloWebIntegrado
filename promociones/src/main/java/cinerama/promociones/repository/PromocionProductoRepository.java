package cinerama.promociones.repository;

import cinerama.promociones.model.PromocionProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromocionProductoRepository extends JpaRepository<PromocionProducto, Long> {

}

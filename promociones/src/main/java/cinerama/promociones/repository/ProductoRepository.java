package cinerama.promociones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cinerama.promociones.model.Producto;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

}
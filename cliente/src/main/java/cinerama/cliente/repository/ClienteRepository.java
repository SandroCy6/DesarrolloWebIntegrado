package cinerama.cliente.repository;

import cinerama.cliente.model.Cliente;
import org.springframework.data.jpa.repository.*;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByDni(String dni);

    boolean existsByDni(String dni);
    
}

package cinerama.catalogo.repositories;

import cinerama.catalogo.models.Asiento;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsientoRepository extends JpaRepository<Asiento, Long> {

    List<Asiento> findBySalaId(Long salaId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Asiento a WHERE a.id = :id")
    Optional<Asiento> findByIdForUpdate(Long id);
}

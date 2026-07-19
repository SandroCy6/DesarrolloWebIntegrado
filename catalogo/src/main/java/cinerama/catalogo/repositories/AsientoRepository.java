package cinerama.catalogo.repositories;

import cinerama.catalogo.models.Asiento;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsientoRepository extends JpaRepository<Asiento, Long> {

    @Query("SELECT a FROM Asiento a WHERE a.horario.sala.id = :salaId")
    List<Asiento> findBySalaId(@Param("salaId") Long salaId);
    List<Asiento> findByHorarioId(Long horarioId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Asiento a WHERE a.id = :id")
    Optional<Asiento> findByIdForUpdate(Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Asiento a WHERE a.horario.id = :horarioId")
    void deleteByHorarioId(@Param("horarioId") Long horarioId);
}

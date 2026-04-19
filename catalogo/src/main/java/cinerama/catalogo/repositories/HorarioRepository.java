package cinerama.catalogo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cinerama.catalogo.models.Horario;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<Horario,Long> {
    // Para ver toda la cartelera de una película en una fecha específica
    List<Horario> findByPeliculaIdAndFecha(Long peliculaId, LocalDate fecha);
    // Para ver los horarios de una sala específica en una fecha (útil para validar cruces de horarios)
    List<Horario> findBySalaIdAndFecha(Long salaId, LocalDate fecha);
}

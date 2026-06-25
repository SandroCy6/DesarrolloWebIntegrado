package cinerama.catalogo.services;

import cinerama.catalogo.dtos.AsientoDTO;
import cinerama.catalogo.models.Asiento;
import cinerama.catalogo.models.EstadoAsiento;
import cinerama.catalogo.models.Horario;
import cinerama.catalogo.models.Sala;
import cinerama.catalogo.repositories.AsientoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AsientoService {
    @Autowired
    private AsientoRepository asientoRepository;

    private List<Asiento> asientos;

    private AsientoDTO convertirADTO(Asiento asiento) {
        AsientoDTO dto = new AsientoDTO();
        dto.setId(asiento.getId());
        dto.setNumero(asiento.getNumero());
        dto.setEstado(asiento.getEstado());
        dto.setPrecio(asiento.getPrecio());
        dto.setSalaId(asiento.getSala().getId());

        if (asiento.getHorario() != null) {
            dto.setHorarioId(asiento.getHorario().getId());
        }
        return dto;
    }
    // NUEVO: Listar asientos por Horario (Para el flujo de compra)
    public List<AsientoDTO> listarPorHorario(Long horarioId) {
        return asientoRepository.findByHorarioId(horarioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Tarea 1: Listar asientos de una sala
    public List<AsientoDTO> listarPorSala(Long salaId) {
        return asientoRepository.findBySalaId(salaId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Tarea 2: Obtener detalle de un asiento (incluye precio)
    public Optional<AsientoDTO> obtenerDetalle(Long asientoId) {
        return asientoRepository.findById(asientoId).map(this::convertirADTO);
    }

    // Tarea 3: Actualizar estado con manejo de concurrencia
    @Transactional
    public AsientoDTO actualizarEstado(Long asientoId, EstadoAsiento nuevoEstado) {
        // 1. Aquí se activa el @Lock pesimista.
        Asiento asiento = asientoRepository.findByIdForUpdate(asientoId)
                .orElseThrow(() -> new RuntimeException("Error: Asiento no encontrado"));

        // 2. Validación de negocio extra: ¿Qué pasa si alguien más nos ganó el
        // milisegundo anterior?
        if (nuevoEstado == EstadoAsiento.OCUPADO && asiento.getEstado() == EstadoAsiento.OCUPADO) {
            throw new IllegalStateException("Concurrencia: El asiento ya fue ocupado por otra transacción.");
        }

        // 3. Todo está bien, actualizamos el estado y guardamos.
        asiento.setEstado(nuevoEstado);
        return convertirADTO(asientoRepository.save(asiento));
    }

    public AsientoDTO crearAsiento(Long salaId,Long horarioId, AsientoDTO dto) {
        Sala sala = new Sala();
        sala.setId(salaId);
        Horario horario = new Horario();
        horario.setId(horarioId);
        Asiento asiento = new Asiento();
        asiento.setNumero(dto.getNumero());
        asiento.setEstado(EstadoAsiento.LIBRE);
        asiento.setPrecio(dto.getPrecio());
        asiento.setSala(sala);
        asiento.setHorario(horario);
        return convertirADTO(asientoRepository.save(asiento));
    }
}

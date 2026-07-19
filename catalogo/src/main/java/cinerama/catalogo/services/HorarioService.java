package cinerama.catalogo.services;

import cinerama.catalogo.dtos.HorarioDTO;
import cinerama.catalogo.models.*;
import cinerama.catalogo.repositories.AsientoRepository;
import cinerama.catalogo.repositories.HorarioRepository;
import cinerama.catalogo.repositories.PeliculaRepository;
import cinerama.catalogo.repositories.SalaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HorarioService {

    @Autowired
    private HorarioRepository horarioRepository;
    @Autowired
    private SalaRepository salaRepository;
    @Autowired
    private PeliculaRepository peliculaRepository;
    @Autowired
    private AsientoRepository asientoRepository;


    private HorarioDTO convertirADTO(Horario horario){
        HorarioDTO dto= new HorarioDTO();

        dto.setId(horario.getId());
        dto.setFecha(horario.getFecha());
        dto.setHoraInicio(horario.getHoraInicio());
        dto.setPrecio(horario.getPrecio());

        dto.setPeliculaId(horario.getPelicula().getId());
        dto.setTituloPelicula(horario.getPelicula().getTitulo());

        dto.setSalaId(horario.getSala().getId());
        dto.setNumeroSala(horario.getSala().getNumero());
        dto.setCineId(horario.getSala().getCine().getId());
        dto.setNombreCine(horario.getSala().getCine().getNombre());

        return dto;
    }

    public List<HorarioDTO> obtenerTodos(){
        return horarioRepository.findAll()
                .stream().map(this::convertirADTO)
                    .collect(Collectors.toList());
    }

    public Optional<HorarioDTO> obtenerPorId(Long id){
        return horarioRepository.findById(id).map(this::convertirADTO);
    }

    public List<HorarioDTO> obtenerPorPeliculaYFecha(Long peliculaId, LocalDate fecha) {
        return horarioRepository.findByPeliculaIdAndFecha(peliculaId, fecha).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public HorarioDTO guardar(Horario horario){
        Sala salaCompleta = salaRepository.findById(horario.getSala().getId())
                .orElseThrow(() -> new RuntimeException("Error: La sala especificada no existe"));

        Pelicula peliculaCompleta = peliculaRepository.findById(horario.getPelicula().getId())
                .orElseThrow(() -> new RuntimeException("Error: La película especificada no existe"));

        horario.setSala(salaCompleta);
        horario.setPelicula(peliculaCompleta);

        // 2. Guardamos el horario primero para que la base de datos le asigne un ID
        Horario horarioGuardado = horarioRepository.save(horario);

        // 3. LOGICA DE GENERACIÓN DE ASIENTOS
        List<Asiento> asientosAGenerar = new ArrayList<>();
        int capacidad = salaCompleta.getCapacidad();
        int asientosPorFila = 10; // Define cuántos asientos habrá por fila (ej: 10)

        for (int i = 0; i < capacidad; i++) {
            // Calculamos la letra de la fila (0 = A, 1 = B, 2 = C...)
            char letraFila = (char) ('A' + (i / asientosPorFila));
            // Calculamos el número de asiento en esa fila (del 1 al 10)
            int numeroAsiento = (i % asientosPorFila) + 1;

            // Ejemplo de formato: "A1", "A2" ... "B1"
            String codigoAsiento = String.format("%c%d", letraFila, numeroAsiento);

            Asiento asiento = new Asiento();
            asiento.setNumero(codigoAsiento);
            asiento.setEstado(EstadoAsiento.LIBRE);
            asiento.setHorario(horarioGuardado); // Lo vinculamos al horario recién creado

            asientosAGenerar.add(asiento);
        }

        // 4. Guardamos todos los asientos generados en lote (Batch)
        asientoRepository.saveAll(asientosAGenerar);

        return convertirADTO(horarioGuardado);
    }

    public void eliminar(Long id){
        asientoRepository.deleteByHorarioId(id);
        horarioRepository.deleteById(id);
    }

    @Transactional
    public HorarioDTO actualizar(Long id, Horario horarioActualizado) {
        // 1. Buscamos el horario que queremos editar
        Horario horarioExistente = horarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: El horario especificado no existe"));

        // 2. Buscamos la película y la sala (igual que al guardar, para evitar nulos)
        Sala salaCompleta = salaRepository.findById(horarioActualizado.getSala().getId())
                .orElseThrow(() -> new RuntimeException("Error: La sala especificada no existe"));

        Pelicula peliculaCompleta = peliculaRepository.findById(horarioActualizado.getPelicula().getId())
                .orElseThrow(() -> new RuntimeException("Error: La película especificada no existe"));

        // 3. Reemplazamos los datos antiguos con los nuevos
        horarioExistente.setFecha(horarioActualizado.getFecha());
        horarioExistente.setHoraInicio(horarioActualizado.getHoraInicio());
        horarioExistente.setPrecio(horarioActualizado.getPrecio());
        horarioExistente.setSala(salaCompleta);
        horarioExistente.setPelicula(peliculaCompleta);

        // 4. Guardamos en la base de datos y lo convertimos a DTO para devolverlo
        return convertirADTO(horarioRepository.save(horarioExistente));
    }

}



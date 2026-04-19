package cinerama.catalogo.services;

import cinerama.catalogo.dtos.HorarioDTO;
import cinerama.catalogo.models.Horario;
import cinerama.catalogo.models.Pelicula;
import cinerama.catalogo.models.Sala;
import cinerama.catalogo.repositories.HorarioRepository;
import cinerama.catalogo.repositories.PeliculaRepository;
import cinerama.catalogo.repositories.SalaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

        return convertirADTO(horarioRepository.save(horario));
    }

    public void eliminar(Long id){
        horarioRepository.deleteById(id);
    }

}



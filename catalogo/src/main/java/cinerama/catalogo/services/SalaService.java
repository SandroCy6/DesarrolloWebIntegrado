package cinerama.catalogo.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import cinerama.catalogo.repositories.CineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cinerama.catalogo.dtos.SalaDTO;
import cinerama.catalogo.models.Sala;
import cinerama.catalogo.repositories.SalaRepository;

@Service
public class SalaService {

    @Autowired
    private SalaRepository salaRepository;
    @Autowired
    private CineRepository cineRepository;

    private SalaDTO convertirADTO(Sala sala) {
        SalaDTO dto = new SalaDTO();
        dto.setId(sala.getId());
        dto.setNumero(sala.getNumero());
        dto.setCapacidad(sala.getCapacidad());
        dto.setCineId(sala.getCine().getId());
        dto.setNombreCine(sala.getCine().getNombre());
        return dto;
    }

    public List<SalaDTO> obtenerTodas(){
        return salaRepository.findAll().stream()
        .map(this::convertirADTO).collect(Collectors.toList());
    }

    public Optional<SalaDTO> obtenerPorId(Long id){
        return salaRepository.findById(id).map(this::convertirADTO);
    }

    public List<SalaDTO> obtenerPorCine(Long cineId){
        return salaRepository.findByCineId(cineId)
        .stream()
            .map(this::convertirADTO).collect(Collectors.toList());
    }

    public SalaDTO guardar(Sala sala){
        cinerama.catalogo.models.Cine cineCompleto = cineRepository.findById(sala.getCine().getId())
                .orElseThrow(() -> new RuntimeException("Error: El cine especificado no existe"));

        sala.setCine(cineCompleto);

        return convertirADTO(salaRepository.save(sala));
    }

    public void eliminar(Long id){
        salaRepository.deleteById(id);
    }

}

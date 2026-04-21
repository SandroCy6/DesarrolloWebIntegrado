package cinerama.catalogo.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cinerama.catalogo.dtos.CineDTO;
import cinerama.catalogo.models.Cine;
import cinerama.catalogo.repositories.CineRepository;

@Service
public class CineService {

    @Autowired
    private CineRepository cineRepository;

    private CineDTO convertirADTO(Cine cine) {
        CineDTO dto = new CineDTO();
        dto.setId(cine.getId());
        dto.setNombre(cine.getNombre());
        dto.setDireccion(cine.getDireccion());
        dto.setCiudad(cine.getCiudad());
        return dto;
    }

    public List<CineDTO> obtenerTodos(){
        return cineRepository.findAll()
            .stream().map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public Optional<CineDTO> obtenerPorId(Long id){
        return cineRepository.findById(id).map(this::convertirADTO);
    }

    public CineDTO guardar(Cine cine){
        return convertirADTO(cineRepository.save(cine));
    }

    public void eliminar(Long id){
        cineRepository.deleteById(id);
    }

}

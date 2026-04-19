package cinerama.catalogo.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cinerama.catalogo.dtos.PeliculaDTO;
import cinerama.catalogo.models.Pelicula;
import cinerama.catalogo.repositories.PeliculaRepository;



@Service
public class PeliculaService {

    @Autowired
    private PeliculaRepository peliculaRepository;

    private PeliculaDTO convertirADTO(Pelicula pelicula) {
        PeliculaDTO dto = new PeliculaDTO();
        dto.setId(pelicula.getId());
        dto.setTitulo(pelicula.getTitulo());
        dto.setSinopsis(pelicula.getSinopsis());
        dto.setGenero(pelicula.getGenero());
        dto.setDuracion(pelicula.getDuracion());
        dto.setImagenUrl(pelicula.getImagenUrl());
        return dto;
    }

    public List<PeliculaDTO> obtenerTodas(){
        return peliculaRepository.findAll()
            .stream()
                .map(this::convertirADTO).collect(Collectors.toList());
    }

    public Optional<PeliculaDTO> obtenerPorId(Long id){
        return peliculaRepository.findById(id).map(this::convertirADTO);
    }

    public List<PeliculaDTO> buscarPorTitulo(String titulo){
        return peliculaRepository.findByTituloContainingIgnoreCase(titulo)
        .stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public List<PeliculaDTO> buscarPorGenero(String genero) {
        return peliculaRepository.findByGenero(genero)
        .stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public PeliculaDTO guardar(Pelicula pelicula){
        return convertirADTO(peliculaRepository.save(pelicula)); 
    }

    public void eliminar(Long id){
        peliculaRepository.deleteById(id);
    }

}

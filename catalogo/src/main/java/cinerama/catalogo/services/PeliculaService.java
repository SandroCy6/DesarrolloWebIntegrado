package cinerama.catalogo.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cinerama.catalogo.dtos.PeliculaDTO;
import cinerama.catalogo.models.Pelicula;
import cinerama.catalogo.repositories.PeliculaRepository;



@Service
public class PeliculaService {

    @Autowired
    private PeliculaRepository peliculaRepository;

    private ObjectMapper objectMapper = new ObjectMapper(); // Herramienta de Spring para leer JSON

    @Value("${tmdb.api.token}")
    private String tmdbToken;

    private PeliculaDTO convertirADTO(Pelicula pelicula) {
        PeliculaDTO dto = new PeliculaDTO();
        dto.setId(pelicula.getId());
        dto.setTitulo(pelicula.getTitulo());
        dto.setSinopsis(pelicula.getSinopsis());
        dto.setGenero(pelicula.getGenero());
        dto.setDuracion(pelicula.getDuracion());
        dto.setImagenUrl(pelicula.getImagenUrl());
        dto.setFechaEstreno(pelicula.getFechaEstreno());
        dto.setTrailerUrl(pelicula.getTrailerUrl());
        return dto;
    }

    public List<PeliculaDTO> obtenerTodas(){
        return peliculaRepository.findAll()
            .stream()
                .map(this::convertirADTO).collect(Collectors.toList());
    }

    public List<PeliculaDTO> listarCartelera(){
        return peliculaRepository.findPeliculasEnCartelera()
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

    public List<PeliculaDTO> obtenerProximosEstrenos() {
        LocalDate hoy = LocalDate.now();
        return peliculaRepository.findByFechaEstrenoAfter(hoy)
                .stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public List<PeliculaDTO> obtenerPorCine(Long cineId) {
        return peliculaRepository.findPeliculasByCineId(cineId)
                .stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public PeliculaDTO guardar(Pelicula pelicula){
        return convertirADTO(peliculaRepository.save(pelicula)); 
    }

    public PeliculaDTO actualizar(Long id, Pelicula peliculaActualizada) {

        Pelicula pelicula = peliculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Película no encontrada con id: " + id));

        pelicula.setTitulo(peliculaActualizada.getTitulo());
        pelicula.setSinopsis(peliculaActualizada.getSinopsis());
        pelicula.setGenero(peliculaActualizada.getGenero());
        pelicula.setDuracion(peliculaActualizada.getDuracion());
        pelicula.setImagenUrl(peliculaActualizada.getImagenUrl());
        pelicula.setFechaEstreno(peliculaActualizada.getFechaEstreno());
        pelicula.setTrailerUrl(peliculaActualizada.getTrailerUrl());

        Pelicula peliculaGuardada = peliculaRepository.save(pelicula);

        return convertirADTO(peliculaGuardada);
    }

    public void eliminar(Long id){
        peliculaRepository.deleteById(id);
    }

    // MÉTODOS DE INTEGRACIÓN CON TMDB

    public String buscarEnTMDB(String query) throws Exception{
        String queryFormateada = query.replace(" ","+");
        String url = "https://api.themoviedb.org/3/search/movie?query="+queryFormateada+"&language=es-MX";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("accept","application/json")
                .header("Authorization","Bearer "+ tmdbToken)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request,HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    public PeliculaDTO importarDesdeTMDB(Long tmdbId) throws Exception{
        Optional<Pelicula> existente = peliculaRepository.findById(tmdbId);
        if(existente.isPresent()){
            return convertirADTO(existente.get());
        }

        String url = "https://api.themoviedb.org/3/movie/" + tmdbId + "?language=es-MX";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("accept","application/json")
                .header("Authorization","Bearer "+ tmdbToken)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request,HttpResponse.BodyHandlers.ofString());

        if(response.statusCode()!=200){
            throw new RuntimeException("Error al conectar con TMDB. Código: " + response.statusCode());
        }
        JsonNode jsonNode = objectMapper.readTree(response.body());

        Pelicula nuevaPelicula = new Pelicula();
        nuevaPelicula.setId(jsonNode.get("id").asLong());
        nuevaPelicula.setTitulo(jsonNode.get("title").asText());
        nuevaPelicula.setSinopsis(jsonNode.get("overview").asText());
        nuevaPelicula.setDuracion(jsonNode.has("runtime")?jsonNode.get("runtime").asInt():0);
        nuevaPelicula.setFechaEstreno(LocalDate.parse(jsonNode.get("release_date").asText()));

        String genero = "Desconocido";
        if(jsonNode.has("genres") && jsonNode.get("genres").size()>0){
            genero = jsonNode.get("genres").get(0).get("name").asText();
        }
        nuevaPelicula.setGenero(genero);

        if(jsonNode.has("poster_path")&& !jsonNode.get("poster_path").isNull()){
            String posterPath = jsonNode.get("poster_path").asText();
            nuevaPelicula.setImagenUrl("https://image.tmdb.org/t/p/w342" + posterPath);
        }

        return  convertirADTO(peliculaRepository.save(nuevaPelicula));
    }

}

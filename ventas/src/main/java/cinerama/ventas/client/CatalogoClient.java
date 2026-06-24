package cinerama.ventas.client;

import cinerama.ventas.dto.HorarioResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import cinerama.ventas.dto.AsientoDTO;

import java.util.Optional;

@FeignClient(name = "catalogo")
public interface CatalogoClient {

    @GetMapping("/catalogo/horarios/{id}")
    Optional<HorarioResponseDTO> obtenerHorario(@PathVariable Long id);

    @GetMapping("/api/cines/asientos/{asientoId}")
    Optional<AsientoDTO> obtenerAsiento(@PathVariable Long asientoId);

    @PutMapping(value = "/api/cines/asientos/{asientoId}/estado", consumes = "application/json")
    void ocuparAsiento(@PathVariable Long asientoId, @RequestBody String estado);
}
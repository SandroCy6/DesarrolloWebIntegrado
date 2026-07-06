package cinerama.ventas.client;

import cinerama.ventas.dto.PromocionResponseDTO;
import cinerama.ventas.dto.ValidarPromoRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "promociones")
public interface PromocionClient {

    @PostMapping("/promociones/validar")
    PromocionResponseDTO validarPromocion(@RequestBody ValidarPromoRequestDTO request);
}

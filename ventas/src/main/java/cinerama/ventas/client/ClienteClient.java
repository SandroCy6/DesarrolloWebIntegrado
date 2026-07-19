package cinerama.ventas.client;

import cinerama.ventas.dto.ClienteRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "cliente")
public interface ClienteClient {

    @PostMapping("/api/clientes/verificar")
    Object verificarORegistrar(@RequestBody ClienteRequestDTO request);
}
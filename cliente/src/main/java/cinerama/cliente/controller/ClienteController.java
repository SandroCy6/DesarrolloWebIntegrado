package cinerama.cliente.controller;

import cinerama.cliente.dto.ClienteRequest;
import cinerama.cliente.dto.ClienteResponse;
import cinerama.cliente.service.ClienteService;
import cinerama.cliente.service.IntentosIpService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {
    private final ClienteService clienteService;
    private final IntentosIpService intentosIpService;

    @PostMapping("/verificar")
    @ResponseStatus(HttpStatus.OK)
    public ClienteResponse verificar(
            @Valid @RequestBody ClienteRequest request,
            HttpServletRequest httpRequest) {
        String ip = obtenerIp(httpRequest);
        request.setIp(ip);
        if (intentosIpService.estaIpBloqueada(ip)) {
            throw new RuntimeException("IP bloqueada temporalmente. Intente mas tarde.");
        }
        return clienteService.verificarORegistrar(request);
    }

    @GetMapping("/{dni}")
    public ClienteResponse buscarPorDni(@PathVariable String dni) {
        return clienteService.buscarPorDni(dni);
    }

    @GetMapping
    public List<ClienteResponse> listar() {
        return clienteService.listar();
    }

    private String obtenerIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


}

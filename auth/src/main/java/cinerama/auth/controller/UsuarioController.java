package cinerama.auth.controller;

import cinerama.auth.dto.UsuarioRequest;
import cinerama.auth.dto.UsuarioResponse;
import cinerama.auth.entity.Usuario;
import cinerama.auth.repository.UsuarioRepository;
import cinerama.auth.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;
    private final UsuarioRepository usuarioRepo;

    @GetMapping
    public List<UsuarioResponse> listar() {
        return service.listar();
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest req) {
        return ResponseEntity.status(201).body(service.crear(req));
    }

    @PutMapping("/{id}")
    public UsuarioResponse actualizar(@PathVariable Long id,
            @Valid @RequestBody UsuarioRequest req) {
        return service.actualizar(id, req);
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id, Authentication auth) {
        Long adminId = getAdminId(auth);
        service.desactivar(id, adminId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication auth) {
        Long adminId = getAdminId(auth);
        service.eliminar(id, adminId);
        return ResponseEntity.noContent().build();
    }

    private Long getAdminId(Authentication auth) {
        return usuarioRepo.findByUsername(auth.getName())
                .map(Usuario::getId)
                .orElseThrow(() -> new RuntimeException("Admin no encontrado"));
    }
}
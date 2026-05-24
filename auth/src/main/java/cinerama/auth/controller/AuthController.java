package cinerama.auth.controller;

import cinerama.auth.dto.LoginRequest;
import cinerama.auth.dto.LoginResponse;
import cinerama.auth.entity.Usuario;
import cinerama.auth.repository.UsuarioRepository;
import cinerama.auth.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepo;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        Usuario u = usuarioRepo.findByUsername(req.getUsername()).orElseThrow();
        String token = jwtUtil.generateToken(u.getUsername(), u.getRol().name());
        return ResponseEntity.ok(new LoginResponse(token, u.getUsername(), u.getRol().name()));
    }
}
package cinerama.auth.service;

import cinerama.auth.dto.UsuarioRequest;
import cinerama.auth.dto.UsuarioResponse;
import cinerama.auth.entity.Usuario;
import cinerama.auth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario u = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        if (!u.isActivo()) {
            throw new DisabledException("El usuario está desactivado");
        }

        return User.builder()
                .username(u.getUsername())
                .password(u.getPassword())
                .authorities(u.getRol().name())
                .build();
    }

    public UsuarioResponse crear(UsuarioRequest req) {
        if (repo.existsByUsername(req.getUsername())) {
            throw new RuntimeException("El username ya está en uso");
        }
        Usuario u = Usuario.builder()
                .username(req.getUsername())
                .password(encoder.encode(req.getPassword()))
                .nombre(req.getNombre())
                .rol(req.getRol())
                .build();
        return toResponse(repo.save(u));
    }

    public UsuarioResponse actualizar(Long id, UsuarioRequest req) {
        Usuario u = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        u.setNombre(req.getNombre());
        u.setRol(req.getRol());
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            u.setPassword(encoder.encode(req.getPassword()));
        }
        return toResponse(repo.save(u));
    }

    public void desactivar(Long id, Long adminId) {
        if (id.equals(adminId)) {
            throw new RuntimeException("No puedes desactivarte a ti mismo");
        }
        Usuario u = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        u.setActivo(false);
        repo.save(u);
    }

    public void eliminar(Long id, Long adminId) {
        if (id.equals(adminId)) {
            throw new RuntimeException("No puedes eliminarte a ti mismo");
        }
        if (!repo.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        repo.deleteById(id);
    }

    public List<UsuarioResponse> listar() {
        return repo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(
                u.getId(),
                u.getUsername(),
                u.getNombre(),
                u.getRol().name(),
                u.isActivo());
    }
}
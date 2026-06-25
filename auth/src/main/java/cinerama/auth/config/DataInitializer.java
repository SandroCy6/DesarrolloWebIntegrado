package cinerama.auth.config;

import cinerama.auth.entity.Rol;
import cinerama.auth.entity.Usuario;
import cinerama.auth.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAdmin(
            UsuarioRepository repo,
            PasswordEncoder encoder) {

        return args -> {
            if (!repo.existsByUsername("admin")) {
                repo.save(
                        Usuario.builder()
                                .username("admin")
                                .password(encoder.encode("admin123"))
                                .nombre("Administrador")
                                .rol(Rol.ROLE_ADMIN)
                                .activo(true)
                                .build()
                );
            }
        };
    }
}
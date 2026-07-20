package cinerama.api_gateway.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // 1. Permitir tu frontend de Angular
        corsConfig.setAllowedOrigins(Arrays.asList(
                "http://localhost:4200",
                "https://cinerama-project.netlify.app"));

        // 2. Permitir todos los métodos (GET, POST, PUT, DELETE, OPTIONS, etc)
        corsConfig.setMaxAge(8000L);
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // 3. Permitir todas las cabeceras (Authorization, Content-Type, etc)
        corsConfig.setAllowedHeaders(Arrays.asList("*"));

        // 4. Permitir credenciales si las llegaras a usar
        corsConfig.setAllowCredentials(true);

        // 5. Aplicar esta regla a TODAS las rutas del Gateway (/**)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
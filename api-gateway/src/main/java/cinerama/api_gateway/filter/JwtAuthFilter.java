package cinerama.api_gateway.filter;

import cinerama.api_gateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        super(Config.class); // ← obligatorio
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            // Permitir preflight CORS
            if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
                return chain.filter(exchange);
            }

            // 1. Extraemos la ruta y el método HTTP de la petición
            String path = exchange.getRequest().getURI().getPath();
            String method = exchange.getRequest().getMethod().name();

            // 2. BYPASS PÚBLICO: Si es un GET hacia el catálogo, lo dejamos pasar libremente
            if ((path.startsWith("/catalogo") || path.startsWith("/api/cines")) && "GET".equalsIgnoreCase(method)) {
                return chain.filter(exchange);
            }
            
            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            // Sin token → 401
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);

            // Token inválido → 401
            if (!jwtUtil.isTokenValid(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // Verificar rol permitido
            String rolUsuario = jwtUtil.extractRol(token);
            List<String> rolesPermitidos = config.getRoles();

            if (!rolesPermitidos.contains(rolUsuario)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            // OK — deja pasar y agrega el rol en header para que el MS lo sepa si quiere
            var request = exchange.getRequest().mutate()
                    .header("X-User-Rol", rolUsuario)
                    .header("X-User-Name", jwtUtil.extractClaims(token).getSubject())
                    .build();

            return chain.filter(exchange.mutate().request(request).build());
        };
    }

    // DESPUÉS — usar ShortcutType.GATHER_LIST
    @Override
    public ShortcutType shortcutType() {
        return ShortcutType.GATHER_LIST;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("roles");
    }

    public static class Config {
        private List<String> roles;

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) { // ← List, no String
            this.roles = roles;
        }
    }
}
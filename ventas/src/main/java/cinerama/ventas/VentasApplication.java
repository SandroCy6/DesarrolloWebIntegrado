package cinerama.ventas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Spring Boot inicia, pero apaga la configuración de seguridad por defecto
@SpringBootApplication (excludeName = {"org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"})
public class VentasApplication {
	public static void main(String[] args) {
        SpringApplication.run(VentasApplication.class, args);
	}
}

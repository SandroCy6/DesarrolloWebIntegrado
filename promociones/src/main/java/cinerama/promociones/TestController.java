package cinerama.promociones;
import java.sql.Connection;
import java.sql.DriverManager;
import org.springframework.web.bind.annotation.GetMapping;

public class TestController {
    @GetMapping("/test-db")
    public String testDB() {
        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/tu_basedatos",
                    "postgres",
                    "1234"
            );
            return "Conexión exitosa 🚀";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}


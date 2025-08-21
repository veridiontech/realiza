package bl.tech.realiza.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Libera CORS para todos os endpoints
                        .allowedOrigins(
                                "http://localhost:3000",
                                "http://localhost:5173",
                                "https://realiza-web-development.onrender.com",
                                "https://realiza-1.onrender.com",
                                "https://sistema.realizaassessoria.com.br"
                        ) // URLs permitidas
                        .allowedMethods("*") // Métodos permitidos
                        .allowedHeaders("*") // Headers permitidos
                        .exposedHeaders("Authorization", "Content-Type") // Headers expostos na resposta
                        .allowCredentials(true) // Permite envio de credenciais (cookies, auth headers)
                        .maxAge(3600); // Tempo de cache do preflight em segundos
            }
        };
    }
}

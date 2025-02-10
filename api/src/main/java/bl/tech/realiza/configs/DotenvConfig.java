package bl.tech.realiza.configs;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {

    @Bean
    public Dotenv dotenv() {
        if (isRunningLocally()) {
            System.out.println("🚀 Executando em LOCALHOST. Carregando variáveis do .env...");
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")
                    .ignoreIfMissing()
                    .load();

            dotenv.entries().forEach(entry -> {
                if (System.getenv(entry.getKey()) == null) {
                    System.setProperty(entry.getKey(), entry.getValue());
                }
            });

            return dotenv;
        } else {
            System.out.println("🌍 Executando na RENDER. Variáveis de ambiente do sistema serão usadas.");
            return null; // Retorna null na produção, pois as variáveis vêm do painel da Render
        }
    }

    private boolean isRunningLocally() {
        String env = System.getenv("ENVIRONMENT");
        return env == null || env.equalsIgnoreCase("LOCAL");
    }
}

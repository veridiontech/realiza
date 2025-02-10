package bl.tech.realiza.configs;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {

    @Bean
    public Dotenv dotenv() {
        boolean isLocal = isRunningLocally();

        Dotenv dotenv = Dotenv.configure()
                .directory("./")  // Localização do .env no localhost
                .ignoreIfMissing() // Ignora erro se não encontrar
                .load();

        if (isLocal) {
            System.out.println("🚀 Executando em LOCALHOST. Carregando variáveis do .env...");
            dotenv.entries().forEach(entry -> {
                if (System.getenv(entry.getKey()) == null) {
                    System.setProperty(entry.getKey(), entry.getValue());
                }
            });
        } else {
            System.out.println("🌍 Executando na RENDER. Variáveis de ambiente do sistema serão usadas.");
        }

        return dotenv;
    }

    private boolean isRunningLocally() {
        // Define se está rodando no localhost com base na variável ENVIRONMENT
        String env = System.getenv("ENVIRONMENT");
        return env == null || env.equalsIgnoreCase("LOCAL");
    }
}

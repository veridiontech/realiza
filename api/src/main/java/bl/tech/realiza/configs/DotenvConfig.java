package bl.tech.realiza.configs;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {

    private static final boolean IS_LOCAL = isRunningLocally();

    @Bean
    public Dotenv dotenv() {
        if (IS_LOCAL) {
            // Carrega o .env apenas se estiver em ambiente LOCAL
            System.out.println("🚀 Executando em LOCALHOST. Carregando variáveis do .env...");
            Dotenv dotenv = Dotenv.configure()
                    .directory("./") // Caminho do .env localmente
                    .ignoreIfMissing() // Ignora erro se não encontrar
                    .load();

            // Configura as variáveis de ambiente manualmente caso não existam no sistema
            dotenv.entries().forEach(entry -> {
                if (System.getenv(entry.getKey()) == null) {
                    System.setProperty(entry.getKey(), entry.getValue());
                }
            });

            return dotenv;
        }

        // Em produção, as variáveis de ambiente do sistema serão usadas automaticamente
        System.out.println("🌍 Executando na RENDER. Variáveis de ambiente do sistema serão usadas.");
        return null; // Retorna null para evitar carregar o Dotenv em produção
    }

    // Verifica se está executando em ambiente local
    private static boolean isRunningLocally() {
        String env = System.getenv("SPRING_ACTIVE_DATABASE");
        return env == null || env.equalsIgnoreCase("local");
    }
}

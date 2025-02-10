package bl.tech.realiza.configs;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {

    @Bean
    public Dotenv dotenv() {
        // Configure and load the .env file
        return Dotenv.configure()
                .directory("./") // Specify the directory where .env is located
                .ignoreIfMissing() // Skip if .env doesn't exist
                .load(); // Finalize and return the Dotenv object
    }
}

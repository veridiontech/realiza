package bl.tech.realiza.configs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class GoogleCloudStorageConfig {

    @Bean
    public Storage googleStorage() throws IOException {
        String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");

        if (credentialsPath == null) {
            Dotenv dotenv = Dotenv.configure()
                    .filename(".env") // opcional, por padrão é ".env"
                    .load();

            credentialsPath = dotenv.get("GOOGLE_APPLICATION_CREDENTIALS");
        }
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath));
        return StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
    }
}

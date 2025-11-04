package bl.tech.realiza.configs;

import bl.tech.realiza.services.GoogleCloudService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Configuration
@Profile("dev")
public class DevConfig {

    // Desabilita a segurança para o perfil 'dev'
    @Bean
    public SecurityFilterChain securityFilterChainDev(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    // Mock para GoogleCloudService para evitar falhas de inicialização
    @Bean
    public GoogleCloudService googleCloudServiceMock() {
        return new GoogleCloudService(null) {
            @Override
            public String uploadFile(MultipartFile file, String folder) throws IOException {
                System.out.println("MOCK: Upload de arquivo para GCP Storage simulado.");
                return folder + "/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            }

            @Override
            public String generateSignedUrl(String objectPath, int minutesToExpire) {
                System.out.println("MOCK: Geração de URL assinada para GCP Storage simulada.");
                return "http://localhost:8080/mock-signed-url/" + objectPath;
            }

            @Override
            public String deleteFile(String objectPath) throws IOException {
                System.out.println("MOCK: Exclusão de arquivo do GCP Storage simulada.");
                return "File deleted";
            }
        };
    }
}

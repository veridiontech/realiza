package bl.tech.realiza.services.api;

import bl.tech.realiza.domains.services.Token;
import bl.tech.realiza.gateways.repositories.api.TokenRepository;
import bl.tech.realiza.services.auth.JwtService;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApiService {

    private final Dotenv dotenv;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;

    public String generateToken(String enterpriseName, String enterpriseCnpj, String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        String secret = System.getenv("SECRET_KEY") != null
                ? System.getenv("SECRET_KEY")
                : dotenv.get( "SECRET_KEY");

        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("SECRET_KEY is not configured in .env");
        }

        if (password.equals(secret)) {
            Token newToken = tokenRepository.save(
                    Token.builder()
                            .enterpriseName(enterpriseName)
                            .enterpriseCnpj(enterpriseCnpj)
                            .token(jwtService.generateExternalToken(enterpriseName, enterpriseCnpj))
                            .build()
            );

            return newToken.getToken();
        }

        throw new SecurityException("Invalid password");
    }
}

package bl.tech.realiza.services.auth;

import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenManagerService {
    private final Map<String, Long> tokenStore = new HashMap<>();
    private static final long TOKEN_EXPIRATION_TIME = 864000000; // 1 dia

    public String generateToken() {
        try {
            // Gerar entrada única
            String input = System.nanoTime() + ":" + Math.random();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            String token = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);

            // Armazenar o token com o timestamp atual
            tokenStore.put(token, System.currentTimeMillis());
            return token;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar o token", e);
        }
    }

    public boolean validateToken(String token) {
        Long tokenTimestamp = tokenStore.get(token);

        if (tokenTimestamp == null) {
            return false; // Token inválido ou não encontrado
        }

        long currentTime = System.currentTimeMillis();

        // Validar se o token está expirado
        if (currentTime - tokenTimestamp > TOKEN_EXPIRATION_TIME) {
            tokenStore.remove(token);
            return false; // Token expirado
        }

        // Token válido
        tokenStore.remove(token);
        return true;
    }
}
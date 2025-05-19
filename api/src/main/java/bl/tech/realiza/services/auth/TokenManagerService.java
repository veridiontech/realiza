package bl.tech.realiza.services.auth;

import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class TokenManagerService {
    private final Set<String> tokenStore = new HashSet<>(); // Apenas armazenar tokens válidos

    public String generateToken() {
        try {
            // Gerar entrada única
            String input = System.nanoTime() + ":" + Math.random();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            String token = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);

            // Armazenar o token com o timestamp atual
            tokenStore.add(token);
            return token;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar o token", e);
        }
    }

    public boolean validateToken(String token) {
        // Verificar se o token está no armazenamento
        if (!tokenStore.contains(token)) {
            return false; // Token inválido ou não encontrado
        }

        // Token válido, mas removê-lo após validação (se necessário)
        tokenStore.remove(token);
        return true;
    }

    public void revokeToken(String token) {
        tokenStore.remove(token);
    }

}
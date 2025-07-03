package bl.tech.realiza.services.auth;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class RandomPasswordService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String NUMBERS = "0123456789";
    private static final int PASSWORD_LENGTH = 8;  // Você pode ajustar o tamanho da senha conforme necessário

    public String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(randomIndex));
        }

        return password.toString();
    }

    public String generateRandom4DigitCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(4);

        for (int i = 0; i < 4; i++) {
            int randomIndex = random.nextInt(NUMBERS.length());
            password.append(NUMBERS.charAt(randomIndex));
        }

        return password.toString();
    }
}

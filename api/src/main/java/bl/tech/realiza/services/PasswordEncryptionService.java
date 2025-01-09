package bl.tech.realiza.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
@RequiredArgsConstructor
public class PasswordEncryptionService {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String encryptPassword(String plainPassword) {
        return encoder.encode(plainPassword);
    }

    public boolean matches(String plainPassword, String encryptedPassword) {
        return encoder.matches(plainPassword, encryptedPassword);
    }
}

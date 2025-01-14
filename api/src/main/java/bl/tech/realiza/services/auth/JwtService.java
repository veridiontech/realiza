package bl.tech.realiza.services.auth;

import bl.tech.realiza.domains.user.User;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class JwtService {

    private final Dotenv dotenv;
    private final String SECRET_KEY;
    private final long EXPIRATION_TIME;

    public JwtService(Dotenv dotenv1, Dotenv dotenv) {
        this.dotenv = dotenv1;
        this.SECRET_KEY = dotenv.get("SECRET_KEY");
        if (this.SECRET_KEY == null || this.SECRET_KEY.isEmpty()) {
            throw new IllegalArgumentException("SECRET_KEY is missing or empty in the environment variables.");
        }

        String expirationTime = dotenv.get("EXPIRATION_TIME");
        if (expirationTime == null || expirationTime.isEmpty()) {
            throw new IllegalArgumentException("EXPIRATION_TIME is missing or empty in the environment variables.");
        }

        try {
            this.EXPIRATION_TIME = Long.parseLong(expirationTime);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("EXPIRATION_TIME must be a valid long value.");
        }
    }


    public String generateToken(String email, User.Role role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role",role.name())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractRole(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }
}

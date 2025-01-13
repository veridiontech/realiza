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

    public JwtService(Dotenv dotenv) {
        this.dotenv = dotenv;
        this.SECRET_KEY = dotenv.get("SECRET_KEY");
        this.EXPIRATION_TIME = Long.parseLong(dotenv.get("EXPIRATION_TIME"));
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

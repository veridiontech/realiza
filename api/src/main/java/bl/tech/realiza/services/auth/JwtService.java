package bl.tech.realiza.services.auth;

import bl.tech.realiza.domains.user.User;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

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


    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("idUser", user.getIdUser());
        claims.put("cpf", user.getCpf());
        claims.put("description", user.getDescription());
        claims.put("password", user.getPassword());
        claims.put("position", user.getPosition());
        claims.put("role", user.getRole().name());
        claims.put("firstName", user.getFirstName());
        claims.put("timeZone", user.getTimeZone().getID());
        claims.put("surname", user.getSurname());
        claims.put("email", user.getEmail());
        claims.put("profilePicture", user.getProfilePicture());
        claims.put("telephone", user.getTelephone());
        claims.put("cellphone", user.getCellphone());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public Map<String, Object> extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
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

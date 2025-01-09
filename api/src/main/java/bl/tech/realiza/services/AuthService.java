package bl.tech.realiza.services;

import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncryptionService passwordService;
    private final JwtService jwtService;

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user == null || !passwordService.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return jwtService.generateToken(user.getEmail(), user.getRole());
    }
}

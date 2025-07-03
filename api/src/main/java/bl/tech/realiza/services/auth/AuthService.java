package bl.tech.realiza.services.auth;

import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.domains.user.UserClient;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.users.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserManagerRepository userManagerRepository;
    private final UserClientRepository userClientRepository;
    private final UserProviderSupplierRepository userProviderSupplierRepository;
    private final UserProviderSubcontractorRepository userProviderSubcontractorRepository;
    private final PasswordEncryptionService passwordService;
    private final JwtService jwtService;

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user == null || !passwordService.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (user.getIsActive() == null || !user.getIsActive()) {
            throw new RuntimeException("Innactive user. Solicite a ativação do seu cadastro.");
        }

        switch (user.getClass().getSimpleName()) {
            case "UserManager":
                return jwtService.generateTokenManager(userManagerRepository.findById(user.getIdUser()).orElseThrow(() ->  new NotFoundException("User not found")));
            case "UserClient":
                return jwtService.generateTokenClient(userClientRepository.findById(user.getIdUser()).orElseThrow(() ->  new NotFoundException("User not found")));
            case "UserProviderSupplier":
                return jwtService.generateTokenSupplier(userProviderSupplierRepository.findById(user.getIdUser()).orElseThrow(() ->  new NotFoundException("User not found")));
            case "UserProviderSubcontractor":
                return jwtService.generateTokenSubcontractor((userProviderSubcontractorRepository.findById(user.getIdUser()).orElseThrow(() ->  new NotFoundException("User not found"))));
            default:
                return jwtService.generateToken(user);
        }
    }
}

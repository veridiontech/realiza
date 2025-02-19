package bl.tech.realiza.services.auth;

import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.services.ChangePasswordRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordRecoveryService {
    private final UserRepository userRepository;
    private final PasswordEncryptionService passwordEncryptionService;

    public String changePassword(String id, String newPassword) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        user.setPassword(passwordEncryptionService.encryptPassword(newPassword));
        userRepository.save(user);

        return "Password updated successfully";
    }
}

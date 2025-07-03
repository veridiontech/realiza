package bl.tech.realiza.services.auth;

import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.services.email.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordRecoveryService {
    private final UserRepository userRepository;
    private final PasswordEncryptionService passwordEncryptionService;
    private final EmailSender emailSender;
    private final RandomPasswordService randomPasswordService;

    public String changePassword(String id, String newPassword) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        user.setPassword(passwordEncryptionService.encryptPassword(newPassword));
        userRepository.save(user);

        return "Password updated successfully";
    }

    public String createAndSendFourDigitCodeToRecoverPassword(String userEmail) {
        String fourDigitCode = randomPasswordService.generateRandom4DigitCode();
        String fourDigitCodeEmail = fourDigitCode + userEmail;
        User user = userRepository.findByEmailAndIsActive(userEmail, true);
        if (user == null) {
            throw new NotFoundException("User not found");
        } else {
            user.setForgotPasswordCode(fourDigitCodeEmail);
            userRepository.save(user);
            emailSender.sendPasswordRecoveryEmail(user.getEmail(), fourDigitCode);
            return "Code sent to e-mail";
        }
    }

    public Boolean validateCode(String forgotPasswordCode, String userEmail) {
        return userRepository.findByForgotPasswordCode(forgotPasswordCode + userEmail) != null;
    }

    public String changePasswordWithCode(String fourDigitCode, String userEmail, String newPassword) {
        String forgotPasswordCode = fourDigitCode + userEmail;
        User user = userRepository.findByEmailAndForgotPasswordCodeAndIsActiveIsTrue(userEmail,forgotPasswordCode)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setPassword(passwordEncryptionService.encryptPassword(newPassword));
        user.setForgotPasswordCode(null);
        userRepository.save(user);

        return "Password updated successfully";
    }
}

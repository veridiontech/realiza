package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.usecases.interfaces.users.CrudUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrudUserImpl implements CrudUser {

    private final UserRepository userRepository;

    @Override
    public String userActivation(String userId, Boolean activation) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (activation) {
            user.setIsActive(true);
            userRepository.save(user);
            return "User activated";
        } else {
            user.setIsActive(false);
            userRepository.save(user);
            return "User inactivated";
        }
    }
}

package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.domains.user.profile.Profile;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.repositories.users.profile.ProfileRepository;
import bl.tech.realiza.gateways.responses.users.UserEmailListResponse;
import bl.tech.realiza.usecases.interfaces.users.CrudUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CrudUserImpl implements CrudUser {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

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

    @Override
    public void fourDigitCodeCheck() {
        List<User> users = userRepository.findAllByForgotPasswordCodeIsNotNull();
        List<User> batch = new ArrayList<>(50);
        for (User user : users) {
            if (ChronoUnit.HOURS.between(user.getForgotPasswordCodeDate(), LocalDateTime.now()) >= 1) {
                user.setForgotPasswordCode(null);
                user.setForgotPasswordCodeDate(null);
                batch.add(user);
            }
            if (batch.size() == 50) {
                userRepository.saveAll(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            userRepository.saveAll(batch);
        }
    }

    @Override
    public List<UserEmailListResponse> findByProfile(String profileId) {
        List<User> users = userRepository.findAllByProfile_Id(profileId);
        return users.stream().map(user -> UserEmailListResponse.builder()
                .id(user.getIdUser())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build()).toList();
    }

    @Override
    public String changeUserProfile(String userId, String profileId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));
        user.setProfile(profile);
        userRepository.save(user);
        return "Profile changed for user with id " + userId;
    }
}

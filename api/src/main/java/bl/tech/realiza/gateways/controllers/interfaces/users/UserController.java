package bl.tech.realiza.gateways.controllers.interfaces.users;

import bl.tech.realiza.gateways.responses.users.UserEmailListResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserController {
    ResponseEntity<String> userActivation(String userId, Boolean activation);
    ResponseEntity<List<UserEmailListResponse>> usersByProfile(String profileId);
    ResponseEntity<String> changeUserProfile(String userId, String profileId);
    ResponseEntity<Void> removeProfilePicture(String userId);
}

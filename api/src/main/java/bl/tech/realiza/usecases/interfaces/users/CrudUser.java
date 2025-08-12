package bl.tech.realiza.usecases.interfaces.users;

import bl.tech.realiza.gateways.responses.users.UserEmailListResponse;

import java.util.List;

public interface CrudUser {
    String userActivation(String userId, Boolean activation);
    void fourDigitCodeCheck();
    List<UserEmailListResponse> findByProfile(String profileId);
    String changeUserProfile(String userId, String profileId);
}

package bl.tech.realiza.gateways.controllers.interfaces.users;

import bl.tech.realiza.gateways.requests.users.UserManagerRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserManagerController {
    ResponseEntity<UserResponseDto> createUserManager(UserManagerRequestDto userManagerRequestDto);
    ResponseEntity<Optional<UserResponseDto>> getOneUserManager(String id);
    ResponseEntity<Page<UserResponseDto>> getAllUsersManager(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<UserResponseDto>> updateUserManager(String id, UserManagerRequestDto userManagerRequestDto);
    ResponseEntity<Void> deleteUserManager(String id);
}

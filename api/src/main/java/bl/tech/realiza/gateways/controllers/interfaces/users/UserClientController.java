package bl.tech.realiza.gateways.controllers.interfaces.users;

import bl.tech.realiza.gateways.requests.users.UserClientRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserClientController {
    ResponseEntity<UserResponseDto> createUserClient(UserClientRequestDto userClientRequestDto);
    ResponseEntity<Optional<UserResponseDto>> getOneUserClient(String id);
    ResponseEntity<Page<UserResponseDto>> getAllUsersClient(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<UserResponseDto>> updateUserClient(UserClientRequestDto userClientRequestDto);
    ResponseEntity<Void> deleteUserClient(String id);
}

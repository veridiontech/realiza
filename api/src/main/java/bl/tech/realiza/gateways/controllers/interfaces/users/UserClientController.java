package bl.tech.realiza.gateways.controllers.interfaces.users;

import bl.tech.realiza.gateways.requests.users.UserClientRequestDto;
import bl.tech.realiza.gateways.responses.users.UserClientResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserClientController {
    ResponseEntity<UserClientResponseDto> createUserClient(UserClientRequestDto userClientRequestDto);
    ResponseEntity<Optional<UserClientResponseDto>> getOneUserClient(String id);
    ResponseEntity<Page<UserClientResponseDto>> getAllUserClients(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<UserClientResponseDto>> updateUserClient(UserClientRequestDto userClientRequestDto);
    ResponseEntity<Void> deleteUserClient(String id);
}

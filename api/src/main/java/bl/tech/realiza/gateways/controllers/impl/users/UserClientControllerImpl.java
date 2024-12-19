package bl.tech.realiza.gateways.controllers.impl.users;

import bl.tech.realiza.gateways.controllers.interfaces.users.UserClientController;
import bl.tech.realiza.gateways.requests.users.UserClientRequestDto;
import bl.tech.realiza.gateways.responses.users.UserClientResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class UserClientControllerImpl implements UserClientController {
    @Override
    public ResponseEntity<UserClientResponseDto> createUserClient(UserClientRequestDto branchRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<UserClientResponseDto>> getOneUserClient(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Page<UserClientResponseDto>> getAllUserClients(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<UserClientResponseDto>> updateUserClient(UserClientRequestDto branchRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteUserClient(String id) {
        return null;
    }
}

package bl.tech.realiza.gateways.controllers.impl.users;

import bl.tech.realiza.gateways.controllers.interfaces.users.UserSubcontractorController;
import bl.tech.realiza.gateways.requests.users.UserSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.users.UserSubcontractorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class UserSubcontractorControllerImpl implements UserSubcontractorController {
    @Override
    public ResponseEntity<UserSubcontractorResponseDto> createUserSubcontractor(UserSubcontractorRequestDto branchRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<UserSubcontractorResponseDto>> getOneUserSubcontractor(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Page<UserSubcontractorResponseDto>> getAllUserSubcontractors(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<UserSubcontractorResponseDto>> updateUserSubcontractor(UserSubcontractorRequestDto branchRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteUserSubcontractor(String id) {
        return null;
    }
}

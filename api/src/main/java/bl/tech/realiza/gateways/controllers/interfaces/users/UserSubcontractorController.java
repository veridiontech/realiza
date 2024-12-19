package bl.tech.realiza.gateways.controllers.interfaces.users;

import bl.tech.realiza.gateways.requests.users.UserSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.users.UserSubcontractorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserSubcontractorController {
    ResponseEntity<UserSubcontractorResponseDto> createUserSubcontractor(UserSubcontractorRequestDto branchRequestDto);
    ResponseEntity<Optional<UserSubcontractorResponseDto>> getOneUserSubcontractor(String id);
    ResponseEntity<Page<UserSubcontractorResponseDto>> getAllUserSubcontractors(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<UserSubcontractorResponseDto>> updateUserSubcontractor(UserSubcontractorRequestDto branchRequestDto);
    ResponseEntity<Void> deleteUserSubcontractor(String id);
}

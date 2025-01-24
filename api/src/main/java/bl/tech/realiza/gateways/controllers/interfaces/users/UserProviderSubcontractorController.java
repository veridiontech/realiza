package bl.tech.realiza.gateways.controllers.interfaces.users;

import bl.tech.realiza.gateways.requests.users.UserClientRequestDto;
import bl.tech.realiza.gateways.requests.users.UserProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserProviderSubcontractorController {
    ResponseEntity<UserResponseDto> createUserProviderSubcontractor(UserProviderSubcontractorRequestDto userProviderSubcontractorRequestDto);
    ResponseEntity<Optional<UserResponseDto>> getOneUserProviderSubcontractor(String id);
    ResponseEntity<Page<UserResponseDto>> getAllUsersProviderSubcontractor(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<UserResponseDto>> updateUserProviderSubcontractor(String id, UserProviderSubcontractorRequestDto userProviderSubcontractorRequestDto);
    ResponseEntity<Void> deleteUserProviderSubcontractor(String id);
    ResponseEntity<Page<UserResponseDto>> getAllUsersProviderSubcontractorBySubcontractor(int page, int size, String sort, Sort.Direction direction, String idSearch);
    ResponseEntity<String> updateUserProviderSubcontractorPassword(String id, UserProviderSubcontractorRequestDto userProviderSubcontractorRequestDto);
}

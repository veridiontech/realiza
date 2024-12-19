package bl.tech.realiza.gateways.controllers.impl.users;

import bl.tech.realiza.gateways.controllers.interfaces.users.UserSubcontractorController;
import bl.tech.realiza.gateways.requests.users.UserSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.users.UserSubcontractorResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/subcontractor")
public class UserSubcontractorControllerImpl implements UserSubcontractorController {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<UserSubcontractorResponseDto> createUserSubcontractor(UserSubcontractorRequestDto branchRequestDto) {
        return null;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<UserSubcontractorResponseDto>> getOneUserSubcontractor(String id) {
        return null;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<UserSubcontractorResponseDto>> getAllUserSubcontractors(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<UserSubcontractorResponseDto>> updateUserSubcontractor(UserSubcontractorRequestDto branchRequestDto) {
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteUserSubcontractor(String id) {
        return null;
    }
}

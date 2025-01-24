package bl.tech.realiza.gateways.controllers.impl.users;

import bl.tech.realiza.gateways.controllers.interfaces.users.UserProviderSubcontractorController;
import bl.tech.realiza.gateways.requests.users.UserProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.usecases.impl.users.CrudUserProviderSubcontractorImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/subcontractor")
@Tag(name = "User Subcontractor")
public class UserProviderSubcontractorControllerImpl implements UserProviderSubcontractorController {

    private final CrudUserProviderSubcontractorImpl crudUserSubcontractor;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<UserResponseDto> createUserProviderSubcontractor(@RequestBody @Valid UserProviderSubcontractorRequestDto userSubcontractorRequestDto) {
        UserResponseDto userSubcontractor = crudUserSubcontractor.save(userSubcontractorRequestDto);

        return ResponseEntity.of(Optional.of(userSubcontractor));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<UserResponseDto>> getOneUserProviderSubcontractor(@PathVariable String id) {
        Optional<UserResponseDto> userSubcontractor = crudUserSubcontractor.findOne(id);

        return ResponseEntity.of(Optional.of(userSubcontractor));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<UserResponseDto>> getAllUsersProviderSubcontractor(@RequestParam(defaultValue = "0") int page,
                                                                                  @RequestParam(defaultValue = "5") int size,
                                                                                  @RequestParam(defaultValue = "idUser") String sort,
                                                                                  @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<UserResponseDto> pageUserSubContractor = crudUserSubcontractor.findAll(pageable);

        return ResponseEntity.ok(pageUserSubContractor);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<UserResponseDto>> updateUserProviderSubcontractor(@PathVariable String id, @RequestBody @Valid UserProviderSubcontractorRequestDto userSubcontractorRequestDto) {
        Optional<UserResponseDto> userSubcontractor = crudUserSubcontractor.update(id, userSubcontractorRequestDto);

        return ResponseEntity.of(Optional.of(userSubcontractor));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteUserProviderSubcontractor(@PathVariable String id) {
        crudUserSubcontractor.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtered-subcontractor")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<UserResponseDto>> getAllUsersProviderSubcontractorBySubcontractor(@RequestParam(defaultValue = "0") int page,
                                                                                                 @RequestParam(defaultValue = "5") int size,
                                                                                                 @RequestParam(defaultValue = "idUser") String sort,
                                                                                                 @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                                                 @RequestParam String idSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<UserResponseDto> pageUserSubContractor = crudUserSubcontractor.findAllBySubcontractor(idSearch, pageable);

        return ResponseEntity.ok(pageUserSubContractor);
    }

    @PutMapping("/change-password/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<String> updateUserProviderSubcontractorPassword(@PathVariable String id, @RequestBody @Valid UserProviderSubcontractorRequestDto userSubcontractorRequestDto) {
        String userSubcontractor = crudUserSubcontractor.changePassword(id, userSubcontractorRequestDto);

        return ResponseEntity.ok(userSubcontractor);
    }
}

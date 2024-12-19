package bl.tech.realiza.gateways.controllers.impl.users;

import bl.tech.realiza.gateways.controllers.interfaces.users.UserSubcontractorController;
import bl.tech.realiza.gateways.requests.users.UserSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.users.UserSubcontractorResponseDto;
import bl.tech.realiza.usecases.impl.users.CrudUserSubcontractorImpl;
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
public class UserSubcontractorControllerImpl implements UserSubcontractorController {

    private final CrudUserSubcontractorImpl crudUserSubcontractor;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<UserSubcontractorResponseDto> createUserSubcontractor(@RequestBody @Valid UserSubcontractorRequestDto userSubcontractorRequestDto) {
        UserSubcontractorResponseDto userSubcontractor = crudUserSubcontractor.save(userSubcontractorRequestDto);

        return ResponseEntity.of(Optional.of(userSubcontractor));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<UserSubcontractorResponseDto>> getOneUserSubcontractor(@PathVariable String id) {
        Optional<UserSubcontractorResponseDto> userSubcontractor = crudUserSubcontractor.findOne(id);

        return ResponseEntity.of(Optional.of(userSubcontractor));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<UserSubcontractorResponseDto>> getAllUserSubcontractors(@RequestParam(defaultValue = "0") int page,
                                                                                       @RequestParam(defaultValue = "5") int size,
                                                                                       @RequestParam(defaultValue = "id") String sort,
                                                                                       @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<UserSubcontractorResponseDto> pageUserSubContractor = crudUserSubcontractor.findAll(pageable);

        return ResponseEntity.ok(pageUserSubContractor);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<UserSubcontractorResponseDto>> updateUserSubcontractor(@RequestBody @Valid UserSubcontractorRequestDto userSubcontractorRequestDto) {
        Optional<UserSubcontractorResponseDto> userSubcontractor = crudUserSubcontractor.update(userSubcontractorRequestDto);

        return ResponseEntity.of(Optional.of(userSubcontractor));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteUserSubcontractor(@PathVariable String id) {
        crudUserSubcontractor.delete(id);

        return null;
    }
}

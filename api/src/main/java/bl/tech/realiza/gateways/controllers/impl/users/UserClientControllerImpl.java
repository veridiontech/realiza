package bl.tech.realiza.gateways.controllers.impl.users;

import bl.tech.realiza.gateways.controllers.interfaces.users.UserClientController;
import bl.tech.realiza.gateways.requests.users.UserClientRequestDto;
import bl.tech.realiza.gateways.responses.users.UserClientResponseDto;
import bl.tech.realiza.usecases.impl.users.CrudUserClientImpl;
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
@RequestMapping("/user/client")
public class UserClientControllerImpl implements UserClientController {

    private final CrudUserClientImpl crudUserClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<UserClientResponseDto> createUserClient(@RequestBody @Valid UserClientRequestDto userClientRequestDto) {
        UserClientResponseDto userClient = crudUserClient.save(userClientRequestDto);

        return ResponseEntity.of(Optional.of(userClient));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<UserClientResponseDto>> getOneUserClient(@PathVariable String id) {
        Optional<UserClientResponseDto> userClient = crudUserClient.findOne(id);

        return ResponseEntity.of(Optional.of(userClient));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<UserClientResponseDto>> getAllUserClients(@RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "5") int size,
                                                                         @RequestParam(defaultValue = "id") String sort,
                                                                         @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<UserClientResponseDto> pageUserClient = crudUserClient.findAll(pageable);

        return ResponseEntity.ok(pageUserClient);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<UserClientResponseDto>> updateUserClient(@RequestBody @Valid UserClientRequestDto userClientRequestDto) {
        Optional<UserClientResponseDto> userClient = crudUserClient.update(userClientRequestDto);

        return ResponseEntity.of(Optional.of(userClient));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteUserClient(@PathVariable String id) {
        crudUserClient.delete(id);

        return null;
    }
}

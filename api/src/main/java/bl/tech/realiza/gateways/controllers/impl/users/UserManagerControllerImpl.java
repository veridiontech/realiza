package bl.tech.realiza.gateways.controllers.impl.users;

import bl.tech.realiza.gateways.controllers.interfaces.users.UserManagerController;
import bl.tech.realiza.gateways.requests.users.UserManagerRequestDto;
import bl.tech.realiza.gateways.requests.users.UserManagerRequestDto;
import bl.tech.realiza.gateways.requests.users.UserManagerRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.usecases.impl.users.CrudUserManagerImpl;
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
@RequestMapping("/user/manager")
@Tag(name = "User Manager")
public class UserManagerControllerImpl implements UserManagerController {

    private final CrudUserManagerImpl crudUserManager;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<UserResponseDto> createUserManager(@RequestBody @Valid UserManagerRequestDto userManagerRequestDto) {
        UserResponseDto userManager = crudUserManager.save(userManagerRequestDto);

        return ResponseEntity.of(Optional.of(userManager));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<UserResponseDto>> getOneUserManager(@PathVariable String id) {
        Optional<UserResponseDto> userManager = crudUserManager.findOne(id);

        return ResponseEntity.of(Optional.of(userManager));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<UserResponseDto>> getAllUsersManager(@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "5") int size,
                                                                    @RequestParam(defaultValue = "idUser") String sort,
                                                                    @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<UserResponseDto> pageUserManager = crudUserManager.findAll(pageable);

        return ResponseEntity.ok(pageUserManager);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<UserResponseDto>> updateUserManager(@PathVariable String id, @RequestBody @Valid UserManagerRequestDto userManagerRequestDto) {
        Optional<UserResponseDto> userManager = crudUserManager.update(id, userManagerRequestDto);

        return ResponseEntity.of(Optional.of(userManager));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteUserManager(@PathVariable String id) {
        crudUserManager.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/change-password/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<String> updateUserManagerPassword(@PathVariable String id, @RequestBody @Valid UserManagerRequestDto userManagerRequestDto) {
        String userManager = crudUserManager.changePassword(id, userManagerRequestDto);

        return ResponseEntity.ok(userManager);
    }
}

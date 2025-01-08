package bl.tech.realiza.gateways.controllers.impl.users;

import bl.tech.realiza.gateways.controllers.interfaces.users.UserProviderSupplierController;
import bl.tech.realiza.gateways.requests.users.UserProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/supplier")
public class UserProviderSupplierControllerImpl implements UserProviderSupplierController {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<UserResponseDto> createUserSupplier(UserProviderSupplierRequestDto userProviderSupplierRequestDto) {
        return null;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<UserResponseDto>> getOneUserSupplier(String id) {
        return null;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<UserResponseDto>> getAllUserSuppliers(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<UserResponseDto>> updateUserSupplier(UserProviderSupplierRequestDto userProviderSupplierRequestDto) {
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteUserSupplier(String id) {
        return null;
    }
}

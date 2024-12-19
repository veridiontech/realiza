package bl.tech.realiza.gateways.controllers.impl.users;

import bl.tech.realiza.gateways.controllers.interfaces.users.UserSupplierController;
import bl.tech.realiza.gateways.requests.users.UserSupplierRequestDto;
import bl.tech.realiza.gateways.responses.users.UserSupplierResponseDto;
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
public class UserSupplierControllerImpl implements UserSupplierController {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<UserSupplierResponseDto> createUserSupplier(UserSupplierRequestDto branchRequestDto) {
        return null;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<UserSupplierResponseDto>> getOneUserSupplier(String id) {
        return null;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<UserSupplierResponseDto>> getAllUserSuppliers(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<UserSupplierResponseDto>> updateUserSupplier(UserSupplierRequestDto branchRequestDto) {
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteUserSupplier(String id) {
        return null;
    }
}

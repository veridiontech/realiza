package bl.tech.realiza.gateways.controllers.impl.users;

import bl.tech.realiza.gateways.controllers.interfaces.users.UserProviderSupplierController;
import bl.tech.realiza.gateways.requests.users.UserClientRequestDto;
import bl.tech.realiza.gateways.requests.users.UserProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.usecases.impl.users.CrudUserProviderSupplierImpl;
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
@RequestMapping("/user/supplier")
@Tag(name = "User Supplier")
public class UserProviderSupplierControllerImpl implements UserProviderSupplierController {

    private final CrudUserProviderSupplierImpl crudUserSupplier;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<UserResponseDto> createUserSupplier(@RequestBody @Valid UserProviderSupplierRequestDto userSupplierRequestDto) {
        UserResponseDto userSupplier = crudUserSupplier.save(userSupplierRequestDto);

        return ResponseEntity.of(Optional.of(userSupplier));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<UserResponseDto>> getOneUserSupplier(@PathVariable String id) {
        Optional<UserResponseDto> userSupplier = crudUserSupplier.findOne(id);

        return ResponseEntity.of(Optional.of(userSupplier));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<UserResponseDto>> getAllUserSuppliers(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "5") int size,
                                                                     @RequestParam(defaultValue = "idUser") String sort,
                                                                     @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<UserResponseDto> pageUserSupplier = crudUserSupplier.findAll(pageable);

        return ResponseEntity.ok(pageUserSupplier);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<UserResponseDto>> updateUserSupplier(@PathVariable String id, @RequestBody @Valid UserProviderSupplierRequestDto userSupplierRequestDto) {
        Optional<UserResponseDto> userSupplier = crudUserSupplier.update(id, userSupplierRequestDto);

        return ResponseEntity.of(Optional.of(userSupplier));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteUserSupplier(@PathVariable String id) {
        crudUserSupplier.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtered-supplier")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<UserResponseDto>> getAllUserSuppliersBySupplier(@RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "5") int size,
                                                                               @RequestParam(defaultValue = "idUser") String sort,
                                                                               @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                               @RequestParam String idSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<UserResponseDto> pageUserSupplier = crudUserSupplier.findAllBySupplier(idSearch, pageable);

        return ResponseEntity.ok(pageUserSupplier);
    }

    @PutMapping("/change-password/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<String> updateUserProviderSupplierPassword(@PathVariable String id, @RequestBody @Valid UserProviderSupplierRequestDto userSupplierRequestDto) {

        String userSupplier = crudUserSupplier.changePassword(id, userSupplierRequestDto);

        return ResponseEntity.ok(userSupplier);
    }
}

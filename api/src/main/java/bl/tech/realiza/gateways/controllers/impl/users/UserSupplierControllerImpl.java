package bl.tech.realiza.gateways.controllers.impl.users;

import bl.tech.realiza.gateways.controllers.interfaces.users.UserSupplierController;
import bl.tech.realiza.gateways.requests.users.UserSupplierRequestDto;
import bl.tech.realiza.gateways.responses.users.UserSupplierResponseDto;
import bl.tech.realiza.usecases.impl.users.CrudUserSupplierImpl;
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
public class UserSupplierControllerImpl implements UserSupplierController {

    private final CrudUserSupplierImpl crudUserSupplier;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<UserSupplierResponseDto> createUserSupplier(@RequestBody @Valid UserSupplierRequestDto userSupplierRequestDto) {
        UserSupplierResponseDto userSupplier = crudUserSupplier.save(userSupplierRequestDto);

        return ResponseEntity.of(Optional.of(userSupplier));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<UserSupplierResponseDto>> getOneUserSupplier(@PathVariable String id) {
        Optional<UserSupplierResponseDto> userSupplier = crudUserSupplier.findOne(id);

        return ResponseEntity.of(Optional.of(userSupplier));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<UserSupplierResponseDto>> getAllUserSuppliers(@RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "5") int size,
                                                                             @RequestParam(defaultValue = "id") String sort,
                                                                             @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<UserSupplierResponseDto> pageUserSupplier = crudUserSupplier.findAll(pageable);

        return ResponseEntity.ok(pageUserSupplier);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<UserSupplierResponseDto>> updateUserSupplier(@RequestBody @Valid UserSupplierRequestDto userSupplierRequestDto) {
        Optional<UserSupplierResponseDto> userSupplier = crudUserSupplier.update(userSupplierRequestDto);

        return ResponseEntity.of(Optional.of(userSupplier));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteUserSupplier(@PathVariable String id) {
        crudUserSupplier.delete(id);

        return null;
    }
}

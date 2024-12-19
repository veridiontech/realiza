package bl.tech.realiza.gateways.controllers.impl.contracts;

import bl.tech.realiza.gateways.controllers.interfaces.contracts.SupplierController;
import bl.tech.realiza.gateways.requests.contracts.ContractSupplierRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractSupplierResponseDto;
import bl.tech.realiza.usecases.impl.contracts.CrudSupplierImpl;
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
@RequestMapping("/supplier")
public class SupplierControllerImpl implements SupplierController {

    private final CrudSupplierImpl crudSupplier;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ContractSupplierResponseDto> createContractSupplier(@RequestBody @Valid ContractSupplierRequestDto contractSupplierRequestDto) {
        ContractSupplierResponseDto supplier = crudSupplier.save(contractSupplierRequestDto);

        return ResponseEntity.of(Optional.of(supplier));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ContractSupplierResponseDto>> getOneContractSupplier(@PathVariable String id) {
        Optional<ContractSupplierResponseDto> contractSupplier = crudSupplier.findOne(id);

        return ResponseEntity.of(Optional.of(contractSupplier));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ContractSupplierResponseDto>> getAllSuppliers(@RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "5") int size,
                                                                             @RequestParam(defaultValue = "id") String sort,
                                                                             @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ContractSupplierResponseDto> pageContractSupplier = crudSupplier.findAll(pageable);

        return ResponseEntity.ok(pageContractSupplier);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ContractSupplierResponseDto>> updateContractSupplier(@RequestBody @Valid ContractSupplierRequestDto contractSupplierRequestDto) {
        Optional<ContractSupplierResponseDto> supplier = crudSupplier.update(contractSupplierRequestDto);

        return ResponseEntity.of(Optional.of(supplier));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteContractSupplier(@PathVariable String id) {
        crudSupplier.delete(id);

        return null;
    }
}

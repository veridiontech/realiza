package bl.tech.realiza.gateways.controllers.impl.contracts;

import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.gateways.controllers.interfaces.contracts.ContractProviderSupplierControlller;
import bl.tech.realiza.gateways.requests.contracts.ContractProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractProviderResponseDto;
import bl.tech.realiza.usecases.impl.contracts.CrudContractProviderSupplierImpl;
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
@RequestMapping("/contract/supplier")
public class ContractProviderSupplierControllerImpl implements ContractProviderSupplierControlller {

    private final CrudContractProviderSupplierImpl crudSupplier;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ContractProviderResponseDto> createContractProviderSupplier(@RequestBody @Valid ContractProviderSupplierRequestDto contractSupplierRequestDto) {
        ContractProviderResponseDto supplier = crudSupplier.save(contractSupplierRequestDto);

        return ResponseEntity.of(Optional.of(supplier));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ContractProviderResponseDto>> getOneContractProviderSupplier(@PathVariable String id) {
        Optional<ContractProviderResponseDto> contractSupplier = crudSupplier.findOne(id);

        return ResponseEntity.of(Optional.of(contractSupplier));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ContractProviderResponseDto>> getAllContractsProviderSupplier(@RequestParam(defaultValue = "0") int page,
                                                                                             @RequestParam(defaultValue = "5") int size,
                                                                                             @RequestParam(defaultValue = "id") String sort,
                                                                                             @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ContractProviderResponseDto> pageContractSupplier = crudSupplier.findAll(pageable);

        return ResponseEntity.ok(pageContractSupplier);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ContractProviderResponseDto>> updateContractProviderSupplier(@RequestBody @Valid ContractProviderSupplierRequestDto contractSupplierRequestDto) {
        Optional<ContractProviderResponseDto> supplier = crudSupplier.update(contractSupplierRequestDto);

        return ResponseEntity.of(Optional.of(supplier));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteContractProviderSupplier(@PathVariable String id) {
        crudSupplier.delete(id);

        return ResponseEntity.noContent().build();
    }
}

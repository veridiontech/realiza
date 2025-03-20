package bl.tech.realiza.gateways.controllers.impl.contracts;

import bl.tech.realiza.gateways.controllers.interfaces.contracts.ContractProviderSupplierControlller;
import bl.tech.realiza.gateways.requests.contracts.ContractAndSupplierCreateRequestDto;
import bl.tech.realiza.gateways.requests.contracts.ContractRequestDto;
import bl.tech.realiza.gateways.requests.contracts.ContractSupplierPostRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractAndSupplierCreateResponseDto;
import bl.tech.realiza.gateways.responses.contracts.ContractResponseDto;
import bl.tech.realiza.gateways.responses.contracts.ContractSupplierResponseDto;
import bl.tech.realiza.usecases.impl.contracts.CrudContractProviderSupplierImpl;
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
@RequestMapping("/contract/supplier")
@Tag(name = "Contract Supplier")
public class ContractProviderSupplierControllerImpl implements ContractProviderSupplierControlller {

    private final CrudContractProviderSupplierImpl crudSupplier;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ContractSupplierResponseDto> createContractProviderSupplier(@RequestBody @Valid ContractSupplierPostRequestDto contractSupplierRequestDto) {
        ContractSupplierResponseDto supplier = crudSupplier.save(contractSupplierRequestDto);

        return ResponseEntity.of(Optional.of(supplier));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ContractResponseDto>> getOneContractProviderSupplier(@PathVariable String id) {
        Optional<ContractResponseDto> contractSupplier = crudSupplier.findOne(id);

        return ResponseEntity.of(Optional.of(contractSupplier));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ContractResponseDto>> getAllContractsProviderSupplier(@RequestParam(defaultValue = "0") int page,
                                                                                     @RequestParam(defaultValue = "5") int size,
                                                                                     @RequestParam(defaultValue = "idContract") String sort,
                                                                                     @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ContractResponseDto> pageContractSupplier = crudSupplier.findAll(pageable);

        return ResponseEntity.ok(pageContractSupplier);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ContractResponseDto>> updateContractProviderSupplier(@PathVariable String id, @RequestBody @Valid ContractRequestDto contractSupplierRequestDto) {
        Optional<ContractResponseDto> supplier = crudSupplier.update(id, contractSupplierRequestDto);

        return ResponseEntity.of(Optional.of(supplier));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteContractProviderSupplier(@PathVariable String id) {
        crudSupplier.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtered-supplier")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ContractResponseDto>> getAllContractsProviderSupplierBySupplier(@RequestParam(defaultValue = "0") int page,
                                                                                               @RequestParam(defaultValue = "5") int size,
                                                                                               @RequestParam(defaultValue = "idContract") String sort,
                                                                                               @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                                               @RequestParam String idSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ContractResponseDto> pageContractSupplier = crudSupplier.findAllBySupplier(idSearch, pageable);

        return ResponseEntity.ok(pageContractSupplier);
    }

    @GetMapping("/filtered-client")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ContractResponseDto>> getAllContractsProviderSupplierByClient(@RequestParam(defaultValue = "0") int page,
                                                                                             @RequestParam(defaultValue = "5") int size,
                                                                                             @RequestParam(defaultValue = "idContract") String sort,
                                                                                             @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                                             @RequestParam String idSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ContractResponseDto> pageContractSupplier = crudSupplier.findAllByClient(idSearch, pageable);

        return ResponseEntity.ok(pageContractSupplier);
    }

    @GetMapping("/filtered-client-and-supplier")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ContractResponseDto>> getAllBySupplierAndBranch(@RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "5") int size,
                                                                               @RequestParam(defaultValue = "idContract") String sort,
                                                                               @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                               @RequestParam String idBranch,
                                                                               @RequestParam String idSupplier) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ContractResponseDto> contractResponse = crudSupplier.findAllBySupplierAndBranch(idSupplier, idBranch, pageable);

        return ResponseEntity.ok(contractResponse);
    }

    @PostMapping("/new-supplier")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ContractAndSupplierCreateResponseDto> createContractAndSupplier(@RequestBody @Valid ContractAndSupplierCreateRequestDto contractAndSupplierCreateRequestDto) {

        ContractAndSupplierCreateResponseDto supplier = crudSupplier.saveContractAndSupplier(contractAndSupplierCreateRequestDto);

        return ResponseEntity.of(Optional.of(supplier));
    }
}

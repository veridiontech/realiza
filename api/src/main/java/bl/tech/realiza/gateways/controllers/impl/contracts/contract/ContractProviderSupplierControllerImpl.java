package bl.tech.realiza.gateways.controllers.impl.contracts.contract;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.gateways.controllers.interfaces.contracts.contract.ContractProviderSupplierControlller;
import bl.tech.realiza.gateways.requests.contracts.ContractAndSupplierCreateRequestDto;
import bl.tech.realiza.gateways.requests.contracts.ContractRequestDto;
import bl.tech.realiza.gateways.requests.contracts.ContractSupplierPostRequestDto;
import bl.tech.realiza.gateways.responses.contracts.contract.*;
import bl.tech.realiza.usecases.interfaces.contracts.contract.CrudContractProviderSupplier;
import io.swagger.v3.oas.annotations.Operation;
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

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contract/supplier")
@Tag(name = "Contract Supplier")
public class ContractProviderSupplierControllerImpl implements ContractProviderSupplierControlller {

    private final CrudContractProviderSupplier crudContractSupplier;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ContractSupplierResponseDto> createContractProviderSupplier(@RequestBody @Valid ContractSupplierPostRequestDto contractSupplierRequestDto) {
        ContractSupplierResponseDto supplier = crudContractSupplier.save(contractSupplierRequestDto);

        return ResponseEntity.of(Optional.of(supplier));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ContractResponseDto>> getOneContractProviderSupplier(@PathVariable String id) {
        Optional<ContractResponseDto> contractSupplier = crudContractSupplier.findOne(id);

        return ResponseEntity.of(Optional.of(contractSupplier));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ContractResponseDto>> getAllContractsProviderSupplier(@RequestParam(defaultValue = "0") int page,
                                                                                     @RequestParam(defaultValue = "5") int size,
                                                                                     @RequestParam(defaultValue = "contractReference") String sort,
                                                                                     @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ContractResponseDto> pageContractSupplier = crudContractSupplier.findAll(pageable);

        return ResponseEntity.ok(pageContractSupplier);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ContractResponseDto>> updateContractProviderSupplier(@PathVariable String id, @RequestBody @Valid ContractRequestDto contractSupplierRequestDto) {
        Optional<ContractResponseDto> supplier = crudContractSupplier.update(id, contractSupplierRequestDto);

        return ResponseEntity.of(Optional.of(supplier));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteContractProviderSupplier(@PathVariable String id) {
        crudContractSupplier.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtered-supplier")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ContractResponseDto>> getAllContractsProviderSupplierBySupplier(@RequestParam(defaultValue = "0") int page,
                                                                                               @RequestParam(defaultValue = "5") int size,
                                                                                               @RequestParam(defaultValue = "contractReference") String sort,
                                                                                               @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                                               @RequestParam String idSearch,
                                                                                               @RequestParam(required = false) List<Contract.IsActive> isActive) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ContractResponseDto> pageContractSupplier = crudContractSupplier.findAllBySupplier(idSearch, isActive, pageable);

        return ResponseEntity.ok(pageContractSupplier);
    }

    @GetMapping("/filtered-client")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ContractResponseDto>> getAllContractsProviderSupplierByClient(@RequestParam(defaultValue = "0") int page,
                                                                                             @RequestParam(defaultValue = "5") int size,
                                                                                             @RequestParam(defaultValue = "contractReference") String sort,
                                                                                             @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                                             @RequestParam String idSearch,
                                                                                             @RequestParam(required = false) List<Contract.IsActive> isActive) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ContractResponseDto> pageContractSupplier = crudContractSupplier.findAllByClient(idSearch, isActive, pageable);

        return ResponseEntity.ok(pageContractSupplier);
    }

    @GetMapping("/filtered-client-and-supplier")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ContractResponseDto>> getAllBySupplierAndBranch(@RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "5") int size,
                                                                               @RequestParam(defaultValue = "contractReference") String sort,
                                                                               @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                               @RequestParam String idBranch,
                                                                               @RequestParam String idSupplier) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ContractResponseDto> contractResponse = crudContractSupplier.findAllBySupplierAndBranch(idSupplier, idBranch, pageable);

        return ResponseEntity.ok(contractResponse);
    }

    @PostMapping("/new-supplier")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ContractAndSupplierCreateResponseDto> createContractAndSupplier(@RequestBody @Valid ContractAndSupplierCreateRequestDto contractAndSupplierCreateRequestDto) {

        ContractAndSupplierCreateResponseDto supplier = crudContractSupplier.saveContractAndSupplier(contractAndSupplierCreateRequestDto);

        return ResponseEntity.of(Optional.of(supplier));
    }

    @GetMapping("/subcontract-permission")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Busca os contratos entre fornecedor e cliente que permitem subcontratação utilizando id da filial")
    @Override
    public ResponseEntity<List<ContractSupplierPermissionResponseDto>> getByBranchAndSubcontractPermission(@RequestParam String idBranch) {
        return ResponseEntity.ok(crudContractSupplier.findAllByBranchAndSubcontractPermission(idBranch));
    }

    @GetMapping("/by-responsible/{responsibleId}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<List<ContractResponsibleResponseDto>> getContractByResponsible(@PathVariable String responsibleId) {
        return ResponseEntity.ok(crudContractSupplier.findAllByResponsible(responsibleId));
    }

    @GetMapping("/{contractId}/update-responsible/{responsibleId}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<String> updateResponsibleFromContract(@PathVariable String contractId, @PathVariable String responsibleId) {
        return ResponseEntity.ok(crudContractSupplier.updateResponsible(contractId,responsibleId));
    }
}

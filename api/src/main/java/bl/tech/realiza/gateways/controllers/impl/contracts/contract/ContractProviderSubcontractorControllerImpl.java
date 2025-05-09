package bl.tech.realiza.gateways.controllers.impl.contracts.contract;

import bl.tech.realiza.gateways.controllers.interfaces.contracts.contract.ContractProviderSubcontractorControlller;
import bl.tech.realiza.gateways.requests.contracts.ContractRequestDto;
import bl.tech.realiza.gateways.requests.contracts.ContractSubcontractorPostRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractSubcontractorResponseDto;
import bl.tech.realiza.usecases.impl.contracts.contract.CrudContractProviderSubcontractorImpl;
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
@RequestMapping("/contract/subcontractor")
@Tag(name = "Contract Subcontractor")
public class ContractProviderSubcontractorControllerImpl implements ContractProviderSubcontractorControlller {

    private final CrudContractProviderSubcontractorImpl crudSubcontractor;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ContractSubcontractorResponseDto> createContractProviderSubcontractor(@RequestBody @Valid ContractSubcontractorPostRequestDto contractSubcontractorRequestDto) {
        ContractSubcontractorResponseDto subcontractor = crudSubcontractor.save(contractSubcontractorRequestDto);

        return ResponseEntity.of(Optional.of(subcontractor));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ContractSubcontractorResponseDto>> getOneContractProviderSubcontractor(@PathVariable String id) {
        Optional<ContractSubcontractorResponseDto> contractSubcontractor = crudSubcontractor.findOne(id);

        return ResponseEntity.of(Optional.of(contractSubcontractor));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ContractSubcontractorResponseDto>> getAllContractsProviderSubcontractor(@RequestParam(defaultValue = "0") int page,
                                                                                                       @RequestParam(defaultValue = "5") int size,
                                                                                                       @RequestParam(defaultValue = "idContract") String sort,
                                                                                                       @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ContractSubcontractorResponseDto> pageContractSubcontractor = crudSubcontractor.findAll(pageable);

        return ResponseEntity.ok(pageContractSubcontractor);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ContractSubcontractorResponseDto>> updateContractProviderSubcontractor(@PathVariable String id, @RequestBody @Valid ContractRequestDto contractSubcontractorRequestDto) {
        Optional<ContractSubcontractorResponseDto> subcontractor = crudSubcontractor.update(id, contractSubcontractorRequestDto);

        return ResponseEntity.of(Optional.of(subcontractor));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteContractProviderSubcontractor(@PathVariable String id) {
        crudSubcontractor.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtered-subcontractor")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ContractSubcontractorResponseDto>> getAllContractsProviderSubcontractorBySupplier(@RequestParam(defaultValue = "0") int page,
                                                                                                                 @RequestParam(defaultValue = "5") int size,
                                                                                                                 @RequestParam(defaultValue = "idContract") String sort,
                                                                                                                 @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                                                                 @RequestParam String idSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ContractSubcontractorResponseDto> pageContractSubcontractor = crudSubcontractor.findAll(pageable);

        return ResponseEntity.ok(pageContractSubcontractor);
    }

    @GetMapping("/filtered-by-supplier")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ContractSubcontractorResponseDto>> getAllBySupplier(@RequestParam(defaultValue = "0") int page,
                                                                                   @RequestParam(defaultValue = "5") int size,
                                                                                   @RequestParam(defaultValue = "idContract") String sort,
                                                                                   @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                                   @RequestParam String idSupplier) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ContractSubcontractorResponseDto> pageContractSubcontractor = crudSubcontractor.findAllBySupplier(idSupplier, pageable);

        return ResponseEntity.ok(pageContractSubcontractor);
    }
}

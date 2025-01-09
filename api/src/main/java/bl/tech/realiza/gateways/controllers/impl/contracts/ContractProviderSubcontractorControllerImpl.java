package bl.tech.realiza.gateways.controllers.impl.contracts;

import bl.tech.realiza.gateways.controllers.interfaces.contracts.ContractProviderSubcontractorControlller;
import bl.tech.realiza.gateways.requests.contracts.ContractProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractProviderResponseDto;
import bl.tech.realiza.usecases.impl.contracts.CrudContractProviderSubcontractorImpl;
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
    public ResponseEntity<ContractProviderResponseDto> createContractProviderSubcontractor(@RequestBody @Valid ContractProviderSubcontractorRequestDto contractSubcontractorRequestDto) {
        ContractProviderResponseDto subcontractor = crudSubcontractor.save(contractSubcontractorRequestDto);

        return ResponseEntity.of(Optional.of(subcontractor));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ContractProviderResponseDto>> getOneContractProviderSubcontractor(@PathVariable String id) {
        Optional<ContractProviderResponseDto> contractSubcontractor = crudSubcontractor.findOne(id);

        return ResponseEntity.of(Optional.of(contractSubcontractor));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ContractProviderResponseDto>> getAllContractsProviderSubcontractor(@RequestParam(defaultValue = "0") int page,
                                                                                                  @RequestParam(defaultValue = "5") int size,
                                                                                                  @RequestParam(defaultValue = "idContract") String sort,
                                                                                                  @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ContractProviderResponseDto> pageContractSubcontractor = crudSubcontractor.findAll(pageable);

        return ResponseEntity.ok(pageContractSubcontractor);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ContractProviderResponseDto>> updateContractProviderSubcontractor(@RequestBody @Valid ContractProviderSubcontractorRequestDto contractSubcontractorRequestDto) {
        Optional<ContractProviderResponseDto> subcontractor = crudSubcontractor.update(contractSubcontractorRequestDto);

        return ResponseEntity.of(Optional.of(subcontractor));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteContractProviderSubcontractor(@PathVariable String id) {
        crudSubcontractor.delete(id);

        return ResponseEntity.noContent().build();
    }
}

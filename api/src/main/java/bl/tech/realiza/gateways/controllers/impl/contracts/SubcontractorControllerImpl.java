package bl.tech.realiza.gateways.controllers.impl.contracts;

import bl.tech.realiza.gateways.controllers.interfaces.contracts.SubcontractorController;
import bl.tech.realiza.gateways.requests.contracts.ContractSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractSubcontractorResponseDto;
import bl.tech.realiza.usecases.impl.contracts.CrudContractSubcontractorImpl;
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
@RequestMapping("/subcontractor")
public class SubcontractorControllerImpl implements SubcontractorController {

    private final CrudContractSubcontractorImpl crudSubcontractor;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ContractSubcontractorResponseDto> createContractSubcontractor(@RequestBody @Valid ContractSubcontractorRequestDto contractSubcontractorRequestDto) {
        ContractSubcontractorResponseDto subcontractor = crudSubcontractor.save(contractSubcontractorRequestDto);

        return ResponseEntity.of(Optional.of(subcontractor));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ContractSubcontractorResponseDto>> getOneContractSubcontractor(@PathVariable String id) {
        Optional<ContractSubcontractorResponseDto> contractSubcontractor = crudSubcontractor.findOne(id);

        return ResponseEntity.of(Optional.of(contractSubcontractor));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ContractSubcontractorResponseDto>> getAllSubcontractors(@RequestParam(defaultValue = "0") int page,
                                                                                       @RequestParam(defaultValue = "5") int size,
                                                                                       @RequestParam(defaultValue = "id") String sort,
                                                                                       @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ContractSubcontractorResponseDto> pageContractSubcontractor = crudSubcontractor.findAll(pageable);

        return ResponseEntity.ok(pageContractSubcontractor);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ContractSubcontractorResponseDto>> updateContractSubcontractor(@RequestBody @Valid ContractSubcontractorRequestDto contractSubcontractorRequestDto) {
        Optional<ContractSubcontractorResponseDto> subcontractor = crudSubcontractor.update(contractSubcontractorRequestDto);

        return ResponseEntity.of(Optional.of(subcontractor));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteContractSubcontractor(@PathVariable String id) {
        crudSubcontractor.delete(id);
        
        return null;
    }
}

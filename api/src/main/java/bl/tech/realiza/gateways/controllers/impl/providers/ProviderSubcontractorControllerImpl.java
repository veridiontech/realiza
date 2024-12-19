package bl.tech.realiza.gateways.controllers.impl.providers;

import bl.tech.realiza.gateways.controllers.interfaces.providers.ProviderSubcontractorController;
import bl.tech.realiza.gateways.requests.providers.ProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderSubcontractorResponseDto;
import bl.tech.realiza.usecases.impl.providers.CrudProviderSubcontractorImpl;
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
public class ProviderSubcontractorControllerImpl implements ProviderSubcontractorController {

    private final CrudProviderSubcontractorImpl crudProviderSubcontractor;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ProviderSubcontractorResponseDto> createProviderSubcontractor(@RequestBody @Valid ProviderSubcontractorRequestDto providerSubcontractorRequestDto) {
        ProviderSubcontractorResponseDto providerSubcontractor = crudProviderSubcontractor.save(providerSubcontractorRequestDto);

        return ResponseEntity.of(Optional.of(providerSubcontractor));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ProviderSubcontractorResponseDto>> getOneProviderSubcontractor(@PathVariable String id) {
        Optional<ProviderSubcontractorResponseDto> providerSubcontractor = crudProviderSubcontractor.findOne(id);

        return ResponseEntity.of(Optional.of(providerSubcontractor));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ProviderSubcontractorResponseDto>> getAllProviderSubcontractors(@RequestParam(defaultValue = "0") int page,
                                                                                               @RequestParam(defaultValue = "5") int size,
                                                                                               @RequestParam(defaultValue = "id") String sort,
                                                                                               @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ProviderSubcontractorResponseDto> pageProviderSubcontractor = crudProviderSubcontractor.findAll(pageable);

        return ResponseEntity.ok(pageProviderSubcontractor);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ProviderSubcontractorResponseDto>> updateProviderSubcontractor(@RequestBody @Valid ProviderSubcontractorRequestDto providerSubcontractorRequestDto) {
        Optional<ProviderSubcontractorResponseDto> providerSubcontractor = crudProviderSubcontractor.update(providerSubcontractorRequestDto);

        return ResponseEntity.of(Optional.of(providerSubcontractor));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteProviderSubcontractor(@PathVariable String id) {
        crudProviderSubcontractor.delete(id);

        return null;
    }
}

package bl.tech.realiza.gateways.controllers.impl.providers;

import bl.tech.realiza.gateways.controllers.interfaces.providers.ProviderSubcontractorController;
import bl.tech.realiza.gateways.requests.providers.ProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderResponseDto;
import bl.tech.realiza.usecases.impl.providers.CrudProviderSubcontractorImpl;
import bl.tech.realiza.usecases.interfaces.providers.CrudProviderSubcontractor;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subcontractor")
@Tag(name = "Subcontractor")
public class ProviderSubcontractorControllerImpl implements ProviderSubcontractorController {

    private final CrudProviderSubcontractor crudProviderSubcontractor;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ProviderResponseDto> createProviderSubcontractor(@RequestBody @Valid ProviderSubcontractorRequestDto providerSubcontractorRequestDto) {
        ProviderResponseDto providerSubcontractor = crudProviderSubcontractor.save(providerSubcontractorRequestDto);

        return ResponseEntity.of(Optional.of(providerSubcontractor));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ProviderResponseDto>> getOneProviderSubcontractor(@PathVariable String id) {
        Optional<ProviderResponseDto> providerSubcontractor = crudProviderSubcontractor.findOne(id);

        return ResponseEntity.of(Optional.of(providerSubcontractor));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ProviderResponseDto>> getAllProvidersSubcontractor(@RequestParam(defaultValue = "0") int page,
                                                                                  @RequestParam(defaultValue = "5") int size,
                                                                                  @RequestParam(defaultValue = "idProvider") String sort,
                                                                                  @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ProviderResponseDto> pageProviderSubcontractor = crudProviderSubcontractor.findAll(pageable);

        return ResponseEntity.ok(pageProviderSubcontractor);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ProviderResponseDto>> updateProviderSubcontractor(@PathVariable String id,
                                                                                     @RequestBody @Valid ProviderSubcontractorRequestDto providerSubcontractorRequestDto) {
        Optional<ProviderResponseDto> providerSubcontractor = crudProviderSubcontractor.update(id, providerSubcontractorRequestDto);

        return ResponseEntity.of(Optional.of(providerSubcontractor));
    }

    @PatchMapping("/change-logo/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<String> updateProviderSubcontractorLog(@PathVariable String id, @RequestPart(value = "file") MultipartFile file) {
        String providerSubcontractor = null;
        try {
            providerSubcontractor = crudProviderSubcontractor.changeLogo(id, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(providerSubcontractor);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteProviderSubcontractor(@PathVariable String id) {
        crudProviderSubcontractor.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtered-supplier")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ProviderResponseDto>> getAllProvidersSubcontractorBySupplier(@RequestParam(defaultValue = "0") int page,
                                                                                            @RequestParam(defaultValue = "5") int size,
                                                                                            @RequestParam(defaultValue = "idProvider") String sort,
                                                                                            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                                            @RequestParam String idSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ProviderResponseDto> pageProviderSubcontractor = crudProviderSubcontractor.findAllBySupplier(idSearch, pageable);

        return ResponseEntity.ok(pageProviderSubcontractor);
    }
}

package bl.tech.realiza.gateways.controllers.impl.providers;

import bl.tech.realiza.gateways.controllers.interfaces.providers.ProviderSupplierController;
import bl.tech.realiza.gateways.requests.providers.ProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderResponseDto;
import bl.tech.realiza.usecases.impl.providers.CrudProviderSupplierImpl;
import bl.tech.realiza.usecases.interfaces.providers.CrudProviderSupplier;
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
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/supplier")
@Tag(name = "Supplier")
public class ProviderSupplierControllerImpl implements ProviderSupplierController {

    private final CrudProviderSupplier crudProviderSupplier;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ProviderResponseDto> createProviderSupplier(@RequestBody @Valid ProviderSupplierRequestDto providerSupplierRequestDto) {
        ProviderResponseDto providerSupplier = crudProviderSupplier.save(providerSupplierRequestDto);

        return ResponseEntity.of(Optional.of(providerSupplier));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ProviderResponseDto>> getOneProviderSupplier(@PathVariable String id) {
        Optional<ProviderResponseDto> providerSupplier = crudProviderSupplier.findOne(id);

        return ResponseEntity.of(Optional.of(providerSupplier));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ProviderResponseDto>> getAllProvidersSupplier(@RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "5") int size,
                                                                             @RequestParam(defaultValue = "idProvider") String sort,
                                                                             @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ProviderResponseDto> pageProviderSupplier = crudProviderSupplier.findAll(pageable);

        return ResponseEntity.ok(pageProviderSupplier);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ProviderResponseDto>> updateProviderSupplier(@PathVariable String id,
                                                                                @RequestBody @Valid ProviderSupplierRequestDto providerSupplierRequestDto) {
        Optional<ProviderResponseDto> providerSupplier = crudProviderSupplier.update(id, providerSupplierRequestDto);

        return ResponseEntity.of(Optional.of(providerSupplier));
    }

    @PatchMapping("/change-logo/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<String> updateProviderSupplierLogo(@PathVariable String id, @RequestPart(value = "file") MultipartFile file) {
        String providerSupplier = null;
        try {
            providerSupplier = crudProviderSupplier.changeLogo(id, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(providerSupplier);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteProviderSupplier(@PathVariable String id) {
        crudProviderSupplier.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtered-client")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ProviderResponseDto>> getAllProvidersSupplierByClient(@RequestParam(defaultValue = "0") int page,
                                                                                     @RequestParam(defaultValue = "5") int size,
                                                                                     @RequestParam(defaultValue = "idProvider") String sort,
                                                                                     @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                                     @RequestParam String idSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ProviderResponseDto> pageProviderSupplier = crudProviderSupplier.findAllByClient(idSearch, pageable);

        return ResponseEntity.ok(pageProviderSupplier);
    }

    @PostMapping("/add-branches/{providerId}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<String> addBranchesToSupplier(@PathVariable String providerId, List<String> branches) {

        String providerSupplierResponse = crudProviderSupplier.addBranch(providerId, branches);

        return ResponseEntity.ok(providerSupplierResponse);
    }
}

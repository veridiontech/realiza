package bl.tech.realiza.gateways.controllers.impl.providers;

import bl.tech.realiza.gateways.controllers.interfaces.providers.ProviderSupplierController;
import bl.tech.realiza.gateways.requests.providers.ProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderSupplierResponseDto;
import bl.tech.realiza.usecases.impl.providers.CrudProviderSupplierImpl;
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
public class ProviderSupplierControllerImpl implements ProviderSupplierController {

    private final CrudProviderSupplierImpl crudProviderSupplier;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ProviderSupplierResponseDto> createProviderSupplier(@RequestBody @Valid ProviderSupplierRequestDto providerSupplierRequestDto) {
        ProviderSupplierResponseDto providerSupplier = crudProviderSupplier.save(providerSupplierRequestDto);

        return ResponseEntity.of(Optional.of(providerSupplier));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ProviderSupplierResponseDto>> getOneProviderSupplier(@PathVariable String id) {
        Optional<ProviderSupplierResponseDto> providerSupplier = crudProviderSupplier.findOne(id);

        return ResponseEntity.of(Optional.of(providerSupplier));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ProviderSupplierResponseDto>> getAllProviderSuppliers(@RequestParam(defaultValue = "0") int page,
                                                                                     @RequestParam(defaultValue = "5") int size,
                                                                                     @RequestParam(defaultValue = "id") String sort,
                                                                                     @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ProviderSupplierResponseDto> pageProviderSupplier = crudProviderSupplier.findAll(pageable);

        return ResponseEntity.ok(pageProviderSupplier);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ProviderSupplierResponseDto>> updateProviderSupplier(@RequestBody @Valid ProviderSupplierRequestDto providerSupplierRequestDto) {
        Optional<ProviderSupplierResponseDto> providerSupplier = crudProviderSupplier.update(providerSupplierRequestDto);

        return ResponseEntity.of(Optional.of(providerSupplier));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteProviderSupplier(@PathVariable String id) {
        crudProviderSupplier.delete(id);

        return null;
    }
}

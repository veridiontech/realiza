package bl.tech.realiza.gateways.controllers.impl.providers;

import bl.tech.realiza.gateways.controllers.interfaces.providers.ProviderSupplierController;
import bl.tech.realiza.gateways.requests.providers.ProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/supplier")
public class ProviderSupplierControllerImpl implements ProviderSupplierController {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ProviderResponseDto> createProviderSupplier(ProviderSupplierRequestDto providerSupplierRequestDto) {
        return null;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ProviderResponseDto>> getOneProviderSupplier(String id) {
        return null;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ProviderResponseDto>> getAllProvidersSupplier(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ProviderResponseDto>> updateProviderSupplier(ProviderSupplierRequestDto providerSupplierRequestDto) {
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteProviderSupplier(String id) {
        return null;
    }
}

package bl.tech.realiza.gateways.controllers.impl.providers;

import bl.tech.realiza.gateways.controllers.interfaces.providers.ProviderSupplierController;
import bl.tech.realiza.gateways.requests.providers.ProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderSupplierResponseDto;
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
    public ResponseEntity<ProviderSupplierResponseDto> createProviderSupplier(ProviderSupplierRequestDto branchRequestDto) {
        return null;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ProviderSupplierResponseDto>> getOneProviderSupplier(String id) {
        return null;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ProviderSupplierResponseDto>> getAllProviderSuppliers(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ProviderSupplierResponseDto>> updateProviderSupplier(ProviderSupplierRequestDto branchRequestDto) {
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteProviderSupplier(String id) {
        return null;
    }
}

package bl.tech.realiza.gateways.controllers.impl.providers;

import bl.tech.realiza.gateways.controllers.interfaces.providers.ProviderSupplierController;
import bl.tech.realiza.gateways.requests.providers.ProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderSupplierResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class ProviderSupplierControllerImpl implements ProviderSupplierController {
    @Override
    public ResponseEntity<ProviderSupplierResponseDto> createProviderSupplier(ProviderSupplierRequestDto branchRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<ProviderSupplierResponseDto>> getOneProviderSupplier(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Page<ProviderSupplierResponseDto>> getAllProviderSuppliers(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<ProviderSupplierResponseDto>> updateProviderSupplier(ProviderSupplierRequestDto branchRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteProviderSupplier(String id) {
        return null;
    }
}

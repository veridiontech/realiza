package bl.tech.realiza.gateways.controllers.interfaces.providers;

import bl.tech.realiza.gateways.requests.providers.ProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderSupplierResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface ProviderSupplierController {
    ResponseEntity<ProviderSupplierResponseDto> createProviderSupplier(ProviderSupplierRequestDto providerSupplierRequestDto);
    ResponseEntity<Optional<ProviderSupplierResponseDto>> getOneProviderSupplier(String id);
    ResponseEntity<Page<ProviderSupplierResponseDto>> getAllProviderSuppliers(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<ProviderSupplierResponseDto>> updateProviderSupplier(ProviderSupplierRequestDto providerSupplierRequestDto);
    ResponseEntity<Void> deleteProviderSupplier(String id);
}

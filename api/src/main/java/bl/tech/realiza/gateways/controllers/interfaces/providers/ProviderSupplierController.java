package bl.tech.realiza.gateways.controllers.interfaces.providers;

import bl.tech.realiza.gateways.requests.providers.ProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProviderSupplierController {
    ResponseEntity<ProviderResponseDto> createProviderSupplier(ProviderSupplierRequestDto providerSupplierRequestDto);
    ResponseEntity<Optional<ProviderResponseDto>> getOneProviderSupplier(String id);
    ResponseEntity<Page<ProviderResponseDto>> getAllProvidersSupplier(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<ProviderResponseDto>> updateProviderSupplier(String id, ProviderSupplierRequestDto providerSupplierRequestDto);
    ResponseEntity<String> updateProviderSupplierLogo(String id, MultipartFile file);
    ResponseEntity<Void> deleteProviderSupplier(String id);
    ResponseEntity<Page<ProviderResponseDto>> getAllProvidersSupplierByClient(int page, int size, String sort, Sort.Direction direction, String idSearch);
    ResponseEntity<String> addBranchesToSupplier(String providerId, List<String> branches);
}

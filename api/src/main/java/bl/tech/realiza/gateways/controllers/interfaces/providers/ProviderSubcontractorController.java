package bl.tech.realiza.gateways.controllers.interfaces.providers;

import bl.tech.realiza.gateways.requests.providers.ProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ProviderSubcontractorController {
    ResponseEntity<ProviderResponseDto> createProviderSubcontractor(ProviderSubcontractorRequestDto providerSubcontractorRequestDto, MultipartFile file);
    ResponseEntity<Optional<ProviderResponseDto>> getOneProviderSubcontractor(String id);
    ResponseEntity<Page<ProviderResponseDto>> getAllProvidersSubcontractor(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<ProviderResponseDto>> updateProviderSubcontractor(String id, ProviderSubcontractorRequestDto providerSubcontractorRequestDto);
    ResponseEntity<String> updateProviderSubcontractorLog(String id, MultipartFile file);
    ResponseEntity<Void> deleteProviderSubcontractor(String id);
    ResponseEntity<Page<ProviderResponseDto>> getAllProvidersSubcontractorBySupplier(int page, int size, String sort, Sort.Direction direction, String idSearch);
}

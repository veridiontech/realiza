package bl.tech.realiza.gateways.controllers.interfaces.providers;

import bl.tech.realiza.gateways.requests.providers.ProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderSubcontractorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface ProviderSubcontractorController {
    ResponseEntity<ProviderSubcontractorResponseDto> createProviderSubcontractor(ProviderSubcontractorRequestDto providerSubcontractorRequestDto);
    ResponseEntity<Optional<ProviderSubcontractorResponseDto>> getOneProviderSubcontractor(String id);
    ResponseEntity<Page<ProviderSubcontractorResponseDto>> getAllProviderSubcontractors(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<ProviderSubcontractorResponseDto>> updateProviderSubcontractor(ProviderSubcontractorRequestDto providerSubcontractorRequestDto);
    ResponseEntity<Void> deleteProviderSubcontractor(String id);
}

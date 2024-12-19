package bl.tech.realiza.usecases.interfaces.providers;

import bl.tech.realiza.gateways.requests.providers.ProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderSubcontractorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudProviderSubcontractor {
    ProviderSubcontractorResponseDto save(ProviderSubcontractorRequestDto providerSubcontractorRequestDto);
    Optional<ProviderSubcontractorResponseDto> findOne(String id);
    Page<ProviderSubcontractorResponseDto> findAll(Pageable pageable);
    Optional<ProviderSubcontractorResponseDto> update(String id, ProviderSubcontractorRequestDto providerSubcontractorRequestDto);
    void delete(String id);
}

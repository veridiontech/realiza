package bl.tech.realiza.usecases.interfaces.providers;

import bl.tech.realiza.gateways.requests.providers.ProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.requests.providers.ProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.gateways.responses.providers.ProviderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudProviderSubcontractor {
    ProviderResponseDto save(ProviderSubcontractorRequestDto providerSubcontractorRequestDto);
    Optional<ProviderResponseDto> findOne(String id);
    Page<ProviderResponseDto> findAll(Pageable pageable);
    Optional<ProviderResponseDto> update(String id, ProviderSubcontractorRequestDto providerSubcontractorRequestDto);
    void delete(String id);
    Page<ProviderResponseDto> findAllBySupplier(String idSearch, Pageable pageable);
}

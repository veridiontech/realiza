package bl.tech.realiza.usecases.interfaces.providers;

import bl.tech.realiza.gateways.requests.providers.ProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.requests.providers.ProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudProvider {
    ProviderResponseDto saveSubcontractor(ProviderSubcontractorRequestDto providerSubcontractorRequestDto);
    ProviderResponseDto saveSupplier(ProviderSupplierRequestDto providerSupplierRequestDto);
    Optional<ProviderResponseDto> findOne(String id);
    Page<ProviderResponseDto> findAll(Pageable pageable);
    Optional<ProviderResponseDto> updateSubcontractor(ProviderSubcontractorRequestDto providerSubcontractorRequestDto);
    Optional<ProviderResponseDto> updateSupplier(ProviderSupplierRequestDto providerSupplierRequestDto);
    void delete(String id);
}

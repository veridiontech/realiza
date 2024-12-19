package bl.tech.realiza.usecases.interfaces.providers;

import bl.tech.realiza.gateways.requests.providers.ProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderSupplierResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudProviderSupplier {
    ProviderSupplierResponseDto save(ProviderSupplierRequestDto providerSupplierRequestDto);
    Optional<ProviderSupplierResponseDto> findOne(String id);
    Page<ProviderSupplierResponseDto> findAll(Pageable pageable);
    Optional<ProviderSupplierResponseDto> update(ProviderSupplierRequestDto providerSupplierRequestDto);
    void delete(String id);
}

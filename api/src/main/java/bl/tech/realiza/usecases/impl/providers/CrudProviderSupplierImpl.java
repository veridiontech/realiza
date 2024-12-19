package bl.tech.realiza.usecases.impl.providers;

import bl.tech.realiza.gateways.requests.providers.ProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderSupplierResponseDto;
import bl.tech.realiza.usecases.interfaces.providers.CrudProviderSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CrudProviderSupplierImpl implements CrudProviderSupplier {
    @Override
    public ProviderSupplierResponseDto save(ProviderSupplierRequestDto providerSupplierRequestDto) {
        return null;
    }

    @Override
    public Optional<ProviderSupplierResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<ProviderSupplierResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<ProviderSupplierResponseDto> update(String id, ProviderSupplierRequestDto providerSupplierRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}

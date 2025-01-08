package bl.tech.realiza.usecases.impl.providers;

import bl.tech.realiza.gateways.requests.providers.ProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.requests.providers.ProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderResponseDto;
import bl.tech.realiza.usecases.interfaces.providers.CrudProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class CrudProviderImpl implements CrudProvider {
    @Override
    public ProviderResponseDto saveSubcontractor(ProviderSubcontractorRequestDto providerSubcontractorRequestDto) {
        return null;
    }

    @Override
    public ProviderResponseDto saveSupplier(ProviderSupplierRequestDto providerSupplierRequestDto) {
        return null;
    }

    @Override
    public Optional<ProviderResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<ProviderResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<ProviderResponseDto> updateSubcontractor(ProviderSubcontractorRequestDto providerSubcontractorRequestDto) {
        return Optional.empty();
    }

    @Override
    public Optional<ProviderResponseDto> updateSupplier(ProviderSupplierRequestDto providerSupplierRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}

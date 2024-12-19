package bl.tech.realiza.usecases.impl.providers;

import bl.tech.realiza.gateways.requests.providers.ProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderSubcontractorResponseDto;
import bl.tech.realiza.usecases.interfaces.providers.CrudProviderSubcontractor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CrudProviderSubcontractorImpl implements CrudProviderSubcontractor {
    @Override
    public ProviderSubcontractorResponseDto save(ProviderSubcontractorRequestDto providerSubcontractorRequestDto) {
        return null;
    }

    @Override
    public Optional<ProviderSubcontractorResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<ProviderSubcontractorResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<ProviderSubcontractorResponseDto> update(String id, ProviderSubcontractorRequestDto providerSubcontractorRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}

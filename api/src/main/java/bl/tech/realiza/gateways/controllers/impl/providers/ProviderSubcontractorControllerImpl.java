package bl.tech.realiza.gateways.controllers.impl.providers;

import bl.tech.realiza.gateways.controllers.interfaces.providers.ProviderSubcontractorController;
import bl.tech.realiza.gateways.requests.providers.ProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderSubcontractorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class ProviderSubcontractorControllerImpl implements ProviderSubcontractorController {
    @Override
    public ResponseEntity<ProviderSubcontractorResponseDto> createProviderSubcontractor(ProviderSubcontractorRequestDto branchRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<ProviderSubcontractorResponseDto>> getOneProviderSubcontractor(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Page<ProviderSubcontractorResponseDto>> getAllProviderSubcontractors(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<ProviderSubcontractorResponseDto>> updateProviderSubcontractor(ProviderSubcontractorRequestDto branchRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteProviderSubcontractor(String id) {
        return null;
    }
}

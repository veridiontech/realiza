package bl.tech.realiza.gateways.controllers.impl.providers;

import bl.tech.realiza.gateways.controllers.interfaces.providers.ProviderSubcontractorController;
import bl.tech.realiza.gateways.requests.providers.ProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subcontractor")
public class ProviderSubcontractorControllerImpl implements ProviderSubcontractorController {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ProviderResponseDto> createProviderSubcontractor(ProviderSubcontractorRequestDto providerSubcontractorRequestDto) {
        return null;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ProviderResponseDto>> getOneProviderSubcontractor(String id) {
        return null;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ProviderResponseDto>> getAllProvidersSubcontractor(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ProviderResponseDto>> updateProviderSubcontractor(ProviderSubcontractorRequestDto providerSubcontractorRequestDto) {
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteProviderSubcontractor(String id) {
        return null;
    }
}

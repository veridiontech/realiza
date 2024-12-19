package bl.tech.realiza.gateways.controllers.impl.documents.providers;

import bl.tech.realiza.gateways.controllers.interfaces.documents.providers.DocumentSubcontractorController;
import bl.tech.realiza.gateways.requests.documents.providers.DocumentSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.documents.providers.DocumentSubcontractorResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/document/subcontractor")
public class DocumentSubcontractorControllerImpl implements DocumentSubcontractorController {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<DocumentSubcontractorResponseDto> createDocumentSubcontractor(DocumentSubcontractorRequestDto documentSubcontractorRequestDto) {
        return null;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentSubcontractorResponseDto>> getOneDocumentSubcontractor(String id) {
        return null;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentSubcontractorResponseDto>> getAllDocumentsSubcontractor(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentSubcontractorResponseDto>> updateDocumentSubcontractor(DocumentSubcontractorRequestDto documentSubcontractorRequestDto) {
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteDocumentSubcontractor(String id) {
        return null;
    }
}

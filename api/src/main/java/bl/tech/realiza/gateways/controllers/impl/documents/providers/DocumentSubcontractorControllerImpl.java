package bl.tech.realiza.gateways.controllers.impl.documents.providers;

import bl.tech.realiza.gateways.controllers.interfaces.documents.providers.DocumentSubcontractorController;
import bl.tech.realiza.gateways.requests.documents.providers.DocumentSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.documents.providers.DocumentSubcontractorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class DocumentSubcontractorControllerImpl implements DocumentSubcontractorController {
    @Override
    public ResponseEntity<DocumentSubcontractorResponseDto> createDocumentSubcontractor(DocumentSubcontractorRequestDto documentSubcontractorRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<DocumentSubcontractorResponseDto>> getOneDocumentSubcontractor(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Page<DocumentSubcontractorResponseDto>> getAllDocumentsSubcontractor(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<DocumentSubcontractorResponseDto>> updateDocumentSubcontractor(DocumentSubcontractorRequestDto documentSubcontractorRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteDocumentSubcontractor(String id) {
        return null;
    }
}

package bl.tech.realiza.gateways.controllers.interfaces.documents.providers;

import bl.tech.realiza.gateways.requests.documents.providers.DocumentSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.documents.providers.DocumentSubcontractorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface DocumentSubcontractorController {
    ResponseEntity<DocumentSubcontractorResponseDto> createDocumentSubcontractor(DocumentSubcontractorRequestDto documentSubcontractorRequestDto);
    ResponseEntity<Optional<DocumentSubcontractorResponseDto>> getOneDocumentSubcontractor(String id);
    ResponseEntity<Page<DocumentSubcontractorResponseDto>> getAllDocumentsSubcontractor(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<DocumentSubcontractorResponseDto>> updateDocumentSubcontractor(DocumentSubcontractorRequestDto documentSubcontractorRequestDto);
    ResponseEntity<Void> deleteDocumentSubcontractor(String id);
}

package bl.tech.realiza.gateways.controllers.interfaces.documents.providers;

import bl.tech.realiza.gateways.requests.documents.providers.DocumentsSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.documents.providers.DocumentsSubcontractorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface DocumentsSubcontractorController {
    ResponseEntity<DocumentsSubcontractorResponseDto> createDocumentSubcontractor(DocumentsSubcontractorRequestDto documentsSubcontractorRequestDto);
    ResponseEntity<Optional<DocumentsSubcontractorResponseDto>> getOneDocumentSubcontractor(String id);
    ResponseEntity<Page<DocumentsSubcontractorResponseDto>> getAllDocumentsSubcontractor(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<DocumentsSubcontractorResponseDto>> updateDocumentSubcontractor(DocumentsSubcontractorRequestDto documentsSubcontractorRequestDto);
    ResponseEntity<Void> deleteDocumentSubcontractor(String id);
}

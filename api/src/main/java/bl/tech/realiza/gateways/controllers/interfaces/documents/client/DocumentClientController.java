package bl.tech.realiza.gateways.controllers.interfaces.documents.client;

import bl.tech.realiza.gateways.requests.documents.client.DocumentClientRequestDto;
import bl.tech.realiza.gateways.responses.documents.client.DocumentClientResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface DocumentClientController {
    ResponseEntity<DocumentClientResponseDto> createDocumentClient(DocumentClientRequestDto documentClientRequestDto);
    ResponseEntity<Optional<DocumentClientResponseDto>> getOneDocumentClient(String id);
    ResponseEntity<Page<DocumentClientResponseDto>> getAllDocumentsClient(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<DocumentClientResponseDto>> updateDocumentClient(DocumentClientRequestDto documentClientRequestDto);
    ResponseEntity<Void> deleteDocumentClient(String id);
}

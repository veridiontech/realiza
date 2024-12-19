package bl.tech.realiza.gateways.controllers.interfaces.documents.client;

import bl.tech.realiza.gateways.requests.documents.client.DocumentsClientRequestDto;
import bl.tech.realiza.gateways.responses.documents.client.DocumentsClientResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface DocumentsClientController {
    ResponseEntity<DocumentsClientResponseDto> createDocumentClient(DocumentsClientRequestDto documentsClientRequestDto);
    ResponseEntity<Optional<DocumentsClientResponseDto>> getOneDocumentClient(String id);
    ResponseEntity<Page<DocumentsClientResponseDto>> getAllDocumentsClient(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<DocumentsClientResponseDto>> updateDocumentClient(DocumentsClientRequestDto documentsClientRequestDto);
    ResponseEntity<Void> deleteDocumentClient(String id);
}

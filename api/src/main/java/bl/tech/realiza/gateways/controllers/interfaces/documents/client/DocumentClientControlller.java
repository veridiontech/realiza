package bl.tech.realiza.gateways.controllers.interfaces.documents.client;

import bl.tech.realiza.gateways.requests.documents.client.DocumentClientRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface DocumentClientControlller {
    ResponseEntity<DocumentResponseDto> createDocumentClient(DocumentClientRequestDto documentClientRequestDto, MultipartFile file);
    ResponseEntity<Optional<DocumentResponseDto>> getOneDocumentClient(String id);
    ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsClient(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<DocumentResponseDto>> updateDocumentClient(String id, DocumentClientRequestDto documentClientRequestDto, MultipartFile file);
    ResponseEntity<Void> deleteDocumentClient(String id);
    ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsClientByClient(int page, int size, String sort, Sort.Direction direction, String idSearch);
}

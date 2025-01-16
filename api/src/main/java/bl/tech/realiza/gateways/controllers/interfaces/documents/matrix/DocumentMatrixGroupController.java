package bl.tech.realiza.gateways.controllers.interfaces.documents.matrix;

import bl.tech.realiza.gateways.requests.documents.matrix.DocumentMatrixGroupRequestDto;
import bl.tech.realiza.gateways.requests.documents.provider.DocumentProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentMatrixResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface DocumentMatrixGroupController {
    ResponseEntity<DocumentMatrixResponseDto> createDocumentMatrixGroup(DocumentMatrixGroupRequestDto documentMatrixGroupRequestDto);
    ResponseEntity<Optional<DocumentMatrixResponseDto>> getOneDocumentMatrixGroup(String id);
    ResponseEntity<Page<DocumentMatrixResponseDto>> getAllDocumentsMatrixGroup(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<DocumentMatrixResponseDto>> updateDocumentMatrixGroup(DocumentMatrixGroupRequestDto documentMatrixGroupRequestDto);
    ResponseEntity<Void> deleteDocumentMatrixGroup(String id);
}

package bl.tech.realiza.gateways.controllers.interfaces.documents.matrix;

import bl.tech.realiza.gateways.requests.documents.matrix.DocumentMatrixRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentMatrixResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface DocumentMatrixController {
    ResponseEntity<DocumentMatrixResponseDto> createDocumentMatrix(DocumentMatrixRequestDto documentMatrixRequestDto);
    ResponseEntity<Optional<DocumentMatrixResponseDto>> getOneDocumentMatrix(String id);
    ResponseEntity<Page<DocumentMatrixResponseDto>> getAllDocumentsMatrix(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<DocumentMatrixResponseDto>> updateDocumentMatrix(DocumentMatrixRequestDto documentMatrixRequestDto);
    ResponseEntity<Void> deleteDocumentMatrix(String id);
    ResponseEntity<Page<DocumentMatrixResponseDto>> getAllDocumentsMatrixBySubgroup(int page, int size, String sort, Sort.Direction direction, String idSearch);
    ResponseEntity<Page<DocumentMatrixResponseDto>> getAllDocumentsMatrixByGroup(int page, int size, String sort, Sort.Direction direction, String idSearch);
}

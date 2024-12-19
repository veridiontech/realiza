package bl.tech.realiza.gateways.controllers.interfaces.documents.employee;

import bl.tech.realiza.gateways.requests.documents.employee.DocumentsEmployeeRequestDto;
import bl.tech.realiza.gateways.responses.documents.employee.DocumentsEmployeeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface DocumentsEmployeeController {
    ResponseEntity<DocumentsEmployeeResponseDto> createDocumentEmployee(DocumentsEmployeeRequestDto documentsEmployeeRequestDto);
    ResponseEntity<Optional<DocumentsEmployeeResponseDto>> getOneDocumentEmployee(String id);
    ResponseEntity<Page<DocumentsEmployeeResponseDto>> getAllDocumentsEmployee(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<DocumentsEmployeeResponseDto>> updateDocumentEmployee(DocumentsEmployeeRequestDto documentsEmployeeRequestDto);
    ResponseEntity<Void> deleteDocumentEmployee(String id);
}

package bl.tech.realiza.gateways.controllers.interfaces.documents.employee;

import bl.tech.realiza.gateways.requests.documents.employee.DocumentEmployeeRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface DocumentEmployeeControlller {
    ResponseEntity<DocumentResponseDto> createDocumentEmployee(DocumentEmployeeRequestDto documentEmployeeRequestDto);
    ResponseEntity<Optional<DocumentResponseDto>> getOneDocumentEmployee(String id);
    ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsEmployee(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<DocumentResponseDto>> updateDocumentEmployee(DocumentEmployeeRequestDto documentEmployeeRequestDto);
    ResponseEntity<Void> deleteDocumentEmployee(String id);
}

package bl.tech.realiza.gateways.controllers.interfaces.documents.employee;

import bl.tech.realiza.gateways.requests.documents.employee.DocumentEmployeeRequestDto;
import bl.tech.realiza.gateways.responses.documents.employee.DocumentEmployeeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface DocumentEmployeeController {
    ResponseEntity<DocumentEmployeeResponseDto> createDocumentEmployee(DocumentEmployeeRequestDto documentEmployeeRequestDto);
    ResponseEntity<Optional<DocumentEmployeeResponseDto>> getOneDocumentEmployee(String id);
    ResponseEntity<Page<DocumentEmployeeResponseDto>> getAllDocumentsEmployee(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<DocumentEmployeeResponseDto>> updateDocumentEmployee(DocumentEmployeeRequestDto documentEmployeeRequestDto);
    ResponseEntity<Void> deleteDocumentEmployee(String id);
}

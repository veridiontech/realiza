package bl.tech.realiza.gateways.controllers.interfaces.documents.employee;

import bl.tech.realiza.gateways.requests.documents.employee.DocumentEmployeeRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface DocumentEmployeeControlller {
    ResponseEntity<DocumentResponseDto> createDocumentEmployee(DocumentEmployeeRequestDto documentEmployeeRequestDto);
    ResponseEntity<Optional<DocumentResponseDto>> getOneDocumentEmployee(String id);
    ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsEmployee(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<DocumentResponseDto>> updateDocumentEmployee(String id, DocumentEmployeeRequestDto documentEmployeeRequestDto);
    ResponseEntity<Void> deleteDocumentEmployee(String id);
    ResponseEntity<Optional<DocumentResponseDto>> uploadDocumentEmployee(String id, MultipartFile file);
    ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsEmployeeByEmployee(int page, int size, String sort, Sort.Direction direction, String idSearch);
    ResponseEntity<DocumentResponseDto> getEmployeeDocuments(String id);
    ResponseEntity<String> updateEmployeeDocuments(String id, List<String> documentList);
    ResponseEntity<String> solicitateNewRequiredDocument(String idEmployee, String idDocument);
    ResponseEntity<String> addRequiredDocument(String idEnterprise, String documentMatrixId);
    ResponseEntity<Void> removeRequiredDocument(String documentId);
}

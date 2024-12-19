package bl.tech.realiza.gateways.controllers.impl.documents.employee;

import bl.tech.realiza.gateways.controllers.interfaces.documents.employee.DocumentEmployeeController;
import bl.tech.realiza.gateways.requests.documents.employee.DocumentEmployeeRequestDto;
import bl.tech.realiza.gateways.responses.documents.employee.DocumentEmployeeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class DocumentEmployeeControllerImpl implements DocumentEmployeeController {
    @Override
    public ResponseEntity<DocumentEmployeeResponseDto> createDocumentEmployee(DocumentEmployeeRequestDto documentEmployeeRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<DocumentEmployeeResponseDto>> getOneDocumentEmployee(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Page<DocumentEmployeeResponseDto>> getAllDocumentsEmployee(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<DocumentEmployeeResponseDto>> updateDocumentEmployee(DocumentEmployeeRequestDto documentEmployeeRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteDocumentEmployee(String id) {
        return null;
    }
}

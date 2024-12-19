package bl.tech.realiza.gateways.controllers.impl.documents.employee;

import bl.tech.realiza.gateways.controllers.interfaces.documents.employee.DocumentEmployeeController;
import bl.tech.realiza.gateways.requests.documents.employee.DocumentEmployeeRequestDto;
import bl.tech.realiza.gateways.responses.documents.employee.DocumentEmployeeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/document/employee")
public class DocumentEmployeeControllerImpl implements DocumentEmployeeController {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<DocumentEmployeeResponseDto> createDocumentEmployee(DocumentEmployeeRequestDto documentEmployeeRequestDto) {
        return null;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentEmployeeResponseDto>> getOneDocumentEmployee(String id) {
        return null;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentEmployeeResponseDto>> getAllDocumentsEmployee(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentEmployeeResponseDto>> updateDocumentEmployee(DocumentEmployeeRequestDto documentEmployeeRequestDto) {
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteDocumentEmployee(String id) {
        return null;
    }
}

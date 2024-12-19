package bl.tech.realiza.gateways.controllers.impl.documents.employee;

import bl.tech.realiza.gateways.controllers.interfaces.documents.employee.DocumentEmployeeController;
import bl.tech.realiza.gateways.requests.documents.employee.DocumentEmployeeRequestDto;
import bl.tech.realiza.gateways.responses.documents.employee.DocumentEmployeeResponseDto;
import bl.tech.realiza.usecases.impl.documents.employee.CrudDocumentEmployeeImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/document/employee")
public class DocumentEmployeeControllerImpl implements DocumentEmployeeController {

    private final CrudDocumentEmployeeImpl crudDocumentEmployeeImpl;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<DocumentEmployeeResponseDto> createDocumentEmployee(@RequestBody @Valid DocumentEmployeeRequestDto documentEmployeeRequestDto) {
        DocumentEmployeeResponseDto documentEmployee = crudDocumentEmployeeImpl.save(documentEmployeeRequestDto);

        return ResponseEntity.of(Optional.of(documentEmployee));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentEmployeeResponseDto>> getOneDocumentEmployee(@PathVariable String id) {
        Optional<DocumentEmployeeResponseDto> documentEmployee = crudDocumentEmployeeImpl.findOne(id);

        return ResponseEntity.of(Optional.of(documentEmployee));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentEmployeeResponseDto>> getAllDocumentsEmployee(@RequestParam(defaultValue = "0") int page,
                                                                                     @RequestParam(defaultValue = "5") int size,
                                                                                     @RequestParam(defaultValue = "id") String sort,
                                                                                     @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentEmployeeResponseDto> pageEmployee = crudDocumentEmployeeImpl.findAll(pageable);

        return ResponseEntity.ok(pageEmployee);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentEmployeeResponseDto>> updateDocumentEmployee(@RequestBody @Valid DocumentEmployeeRequestDto documentEmployeeRequestDto) {
        Optional<DocumentEmployeeResponseDto> documentEmployee = crudDocumentEmployeeImpl.update(documentEmployeeRequestDto);

        return ResponseEntity.of(Optional.of(documentEmployee));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteDocumentEmployee(@PathVariable String id) {
        crudDocumentEmployeeImpl.delete(id);

        return null;
    }
}

package bl.tech.realiza.gateways.controllers.impl.documents.employee;

import bl.tech.realiza.gateways.controllers.interfaces.documents.employee.DocumentEmployeeControlller;
import bl.tech.realiza.gateways.requests.documents.employee.DocumentEmployeeRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.usecases.impl.documents.employee.CrudDocumentEmployeeImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/document/employee")
@Tag(name = "Document Employee")
public class DocumentEmployeeControllerImpl implements DocumentEmployeeControlller {

    private final CrudDocumentEmployeeImpl crudDocumentEmployeeImpl;

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<DocumentResponseDto> createDocumentEmployee(
            @RequestPart("documentEmployeeRequestDto") @Valid DocumentEmployeeRequestDto documentEmployeeRequestDto,
            @RequestParam("file") MultipartFile file) {
        DocumentResponseDto documentEmployee = null;
        try {
            documentEmployee = crudDocumentEmployeeImpl.save(documentEmployeeRequestDto, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.of(Optional.of(documentEmployee));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentResponseDto>> getOneDocumentEmployee(@PathVariable String id) {
        Optional<DocumentResponseDto> documentEmployee = crudDocumentEmployeeImpl.findOne(id);

        return ResponseEntity.of(Optional.of(documentEmployee));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsEmployee(@RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "5") int size,
                                                                             @RequestParam(defaultValue = "idDocumentation") String sort,
                                                                             @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentResponseDto> pageEmployee = crudDocumentEmployeeImpl.findAll(pageable);

        return ResponseEntity.ok(pageEmployee);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentResponseDto>> updateDocumentEmployee(
            @RequestPart("documentEmployeeRequestDto")
            @Valid DocumentEmployeeRequestDto documentEmployeeRequestDto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        Optional<DocumentResponseDto> documentEmployee = null;
        try {
            documentEmployee = crudDocumentEmployeeImpl.update(documentEmployeeRequestDto, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.of(Optional.of(documentEmployee));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteDocumentEmployee(@PathVariable String id) {
        crudDocumentEmployeeImpl.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtered-employee")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsEmployeeByEmployee(@RequestParam(defaultValue = "0") int page,
                                                                                       @RequestParam(defaultValue = "5") int size,
                                                                                       @RequestParam(defaultValue = "idDocumentation") String sort,
                                                                                       @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                                       @RequestParam String idSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentResponseDto> pageEmployee = crudDocumentEmployeeImpl.findAllByEmployee(idSearch, pageable);

        return ResponseEntity.ok(pageEmployee);
    }
}

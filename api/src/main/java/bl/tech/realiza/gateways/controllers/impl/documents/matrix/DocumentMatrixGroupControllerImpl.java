package bl.tech.realiza.gateways.controllers.impl.documents.matrix;

import bl.tech.realiza.domains.documents.matrix.DocumentMatrixGroup;
import bl.tech.realiza.gateways.controllers.interfaces.documents.matrix.DocumentMatrixGroupController;
import bl.tech.realiza.gateways.requests.documents.matrix.DocumentMatrixGroupRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentMatrixResponseDto;
import bl.tech.realiza.usecases.impl.documents.matrix.CrudDocumentMatrixGroupImpl;
import bl.tech.realiza.usecases.interfaces.documents.matrix.CrudDocumentMatrixGroup;
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

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/document/matrix/group")
@Tag(name = "Document Matrix Group")
public class DocumentMatrixGroupControllerImpl implements DocumentMatrixGroupController {

    private final CrudDocumentMatrixGroup crudDocumentMatrixGroup;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<DocumentMatrixResponseDto> createDocumentMatrixGroup(@RequestBody @Valid DocumentMatrixGroupRequestDto documentMatrixGroupRequestDto) {
        DocumentMatrixResponseDto documentMatrixResponseGroup = crudDocumentMatrixGroup.save(documentMatrixGroupRequestDto);
        return ResponseEntity.of(Optional.of(documentMatrixResponseGroup));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentMatrixResponseDto>> getOneDocumentMatrixGroup(@PathVariable String id) {
        Optional<DocumentMatrixResponseDto> documentMatrixResponseGroup = crudDocumentMatrixGroup.findOne(id);
        return ResponseEntity.of(Optional.of(documentMatrixResponseGroup));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentMatrixResponseDto>> getAllDocumentsMatrixGroup(@RequestParam(defaultValue = "0") int page,
                                                                                      @RequestParam(defaultValue = "5") int size,
                                                                                      @RequestParam(defaultValue = "groupName") String sort,
                                                                                      @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentMatrixResponseDto> pageDocumentMatrixGroup = crudDocumentMatrixGroup.findAll(pageable);
        return ResponseEntity.ok(pageDocumentMatrixGroup);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentMatrixResponseDto>> updateDocumentMatrixGroup(@PathVariable String id, @RequestBody @Valid DocumentMatrixGroupRequestDto documentMatrixGroupRequestDto) {
        Optional<DocumentMatrixResponseDto> documentMatrixGroup = crudDocumentMatrixGroup.update(id, documentMatrixGroupRequestDto);
        return ResponseEntity.of(Optional.of(documentMatrixGroup));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteDocumentMatrixGroup(@PathVariable String id) {
        crudDocumentMatrixGroup.delete(id);
        return ResponseEntity.noContent().build();
    }
}

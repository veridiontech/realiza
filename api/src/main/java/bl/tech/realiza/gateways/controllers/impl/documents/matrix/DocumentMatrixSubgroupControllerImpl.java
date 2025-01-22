package bl.tech.realiza.gateways.controllers.impl.documents.matrix;

import bl.tech.realiza.gateways.controllers.interfaces.documents.matrix.DocumentMatrixSubgroupController;
import bl.tech.realiza.gateways.requests.documents.matrix.DocumentMatrixSubgroupRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentMatrixResponseDto;
import bl.tech.realiza.usecases.impl.documents.matrix.CrudDocumentMatrixSubgroupImpl;
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
@RequestMapping("/document/matrix/subgroup")
@Tag(name = "Document Matrix Subgroup")
public class DocumentMatrixSubgroupControllerImpl implements DocumentMatrixSubgroupController {

    private final CrudDocumentMatrixSubgroupImpl documentMatrixSubgroup;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<DocumentMatrixResponseDto> createDocumentMatrixSubgroup(@RequestBody @Valid DocumentMatrixSubgroupRequestDto documentMatrixSubgroupRequestDto) {
        DocumentMatrixResponseDto documentMatrixResponseGroup = documentMatrixSubgroup.save(documentMatrixSubgroupRequestDto);
        return ResponseEntity.of(Optional.of(documentMatrixResponseGroup));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentMatrixResponseDto>> getOneDocumentMatrixSubgroup(@PathVariable String id) {
        Optional<DocumentMatrixResponseDto> documentMatrixResponseGroup = documentMatrixSubgroup.findOne(id);
        return ResponseEntity.of(Optional.of(documentMatrixResponseGroup));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentMatrixResponseDto>> getAllDocumentsMatrixSubgroup(@RequestParam(defaultValue = "0") int page,
                                                                                         @RequestParam(defaultValue = "5") int size,
                                                                                         @RequestParam(defaultValue = "idDocumentSubgroup") String sort,
                                                                                         @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentMatrixResponseDto> pageDocumentMatrixGroup = documentMatrixSubgroup.findAll(pageable);
        return ResponseEntity.ok(pageDocumentMatrixGroup);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentMatrixResponseDto>> updateDocumentMatrixSubgroup(@RequestBody @Valid DocumentMatrixSubgroupRequestDto documentMatrixSubgroupRequestDto) {
        Optional<DocumentMatrixResponseDto> documentMatrixGroup = documentMatrixSubgroup.update(documentMatrixSubgroupRequestDto);
        return ResponseEntity.of(Optional.of(documentMatrixGroup));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteDocumentMatrixSubgroup(@PathVariable String id) {
        documentMatrixSubgroup.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtered-group")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentMatrixResponseDto>> getAllDocumentsMatrixSubgroupByGroup(@RequestParam(defaultValue = "0") int page,
                                                                                                @RequestParam(defaultValue = "5") int size,
                                                                                                @RequestParam(defaultValue = "idDocumentSubgroup") String sort,
                                                                                                @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                                                @RequestParam String idSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentMatrixResponseDto> pageDocumentMatrixGroup = documentMatrixSubgroup.findAllByGroup(idSearch, pageable);
        return ResponseEntity.ok(pageDocumentMatrixGroup);
    }
}

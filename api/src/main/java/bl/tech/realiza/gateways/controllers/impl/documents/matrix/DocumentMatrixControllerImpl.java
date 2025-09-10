package bl.tech.realiza.gateways.controllers.impl.documents.matrix;

import bl.tech.realiza.gateways.controllers.interfaces.documents.matrix.DocumentMatrixController;
import bl.tech.realiza.gateways.requests.documents.matrix.DocumentMatrixRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentMatrixResponseDto;
import bl.tech.realiza.usecases.impl.documents.matrix.CrudDocumentMatrixImpl;
import bl.tech.realiza.usecases.interfaces.documents.matrix.CrudDocumentMatrix;
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
@RequestMapping("/document/matrix")
@Tag(name = "Document Matrix")
public class DocumentMatrixControllerImpl implements DocumentMatrixController {

    private final CrudDocumentMatrix crudDocumentMatrix;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<DocumentMatrixResponseDto> createDocumentMatrix(@RequestBody @Valid DocumentMatrixRequestDto documentMatrixRequestDto) {
        DocumentMatrixResponseDto documentMatrixResponse = crudDocumentMatrix.save(documentMatrixRequestDto);
        return ResponseEntity.of(Optional.of(documentMatrixResponse));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentMatrixResponseDto>> getOneDocumentMatrix(@PathVariable String id) {
        Optional<DocumentMatrixResponseDto> documentMatrixResponse = crudDocumentMatrix.findOne(id);
        return ResponseEntity.of(Optional.of(documentMatrixResponse));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentMatrixResponseDto>> getAllDocumentsMatrix(@RequestParam(defaultValue = "0") int page,
                                                                                 @RequestParam(defaultValue = "5") int size,
                                                                                 @RequestParam(defaultValue = "name") String sort,
                                                                                 @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentMatrixResponseDto> pageDocumentMatrix = crudDocumentMatrix.findAll(pageable);
        return ResponseEntity.ok(pageDocumentMatrix);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentMatrixResponseDto>> updateDocumentMatrix(@PathVariable String id,
                                                                                    @RequestParam(required = false) Boolean replicate,
                                                                                    @RequestBody @Valid DocumentMatrixRequestDto documentMatrixRequestDto) {
        Optional<DocumentMatrixResponseDto> documentMatrix = crudDocumentMatrix.update(id, replicate, documentMatrixRequestDto);
        return ResponseEntity.of(Optional.of(documentMatrix));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteDocumentMatrix(@PathVariable String id) {
        crudDocumentMatrix.delete(id);
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/filtered-subgroup")
//    @ResponseStatus(HttpStatus.OK)
//    @Override
//    public ResponseEntity<Page<DocumentMatrixResponseDto>> getAllDocumentsMatrixBySubgroup(@RequestParam(defaultValue = "0") int page,
//                                                                                 @RequestParam(defaultValue = "5") int size,
//                                                                                 @RequestParam(defaultValue = "name") String sort,
//                                                                                 @RequestParam(defaultValue = "ASC") Sort.Direction direction,
//                                                                                 @RequestParam String idSearch) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));
//
//        Page<DocumentMatrixResponseDto> pageDocumentMatrix = crudDocumentMatrix.findAllBySubgroup(idSearch, pageable);
//        return ResponseEntity.ok(pageDocumentMatrix);
//    }

    @GetMapping("/filtered-group")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentMatrixResponseDto>> getAllDocumentsMatrixByGroup(@RequestParam(defaultValue = "0") int page,
                                                                                 @RequestParam(defaultValue = "5") int size,
                                                                                 @RequestParam(defaultValue = "name") String sort,
                                                                                 @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                                 @RequestParam String idSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentMatrixResponseDto> pageDocumentMatrix = crudDocumentMatrix.findAllByGroup(idSearch, pageable);
        return ResponseEntity.ok(pageDocumentMatrix);
    }
}

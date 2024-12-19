package bl.tech.realiza.gateways.controllers.impl.documents.providers;

import bl.tech.realiza.gateways.controllers.interfaces.documents.providers.DocumentSubcontractorController;
import bl.tech.realiza.gateways.requests.documents.providers.DocumentSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.documents.providers.DocumentSubcontractorResponseDto;
import bl.tech.realiza.usecases.impl.documents.providers.CrudDocumentSubcontractorImpl;
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
@RequestMapping("/document/subcontractor")
public class DocumentSubcontractorControllerImpl implements DocumentSubcontractorController {

    private final CrudDocumentSubcontractorImpl crudDocumentSubcontractor;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<DocumentSubcontractorResponseDto> createDocumentSubcontractor(@RequestBody @Valid DocumentSubcontractorRequestDto documentSubcontractorRequestDto) {
        DocumentSubcontractorResponseDto documentSubcontractor = crudDocumentSubcontractor.save(documentSubcontractorRequestDto);

        return ResponseEntity.of(Optional.of(documentSubcontractor));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentSubcontractorResponseDto>> getOneDocumentSubcontractor(@PathVariable String id) {
        Optional<DocumentSubcontractorResponseDto> documentSubcontractor = crudDocumentSubcontractor.findOne(id);

        return ResponseEntity.of(Optional.of(documentSubcontractor));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentSubcontractorResponseDto>> getAllDocumentsSubcontractor(@RequestParam(defaultValue = "0") int page,
                                                                                               @RequestParam(defaultValue = "5") int size,
                                                                                               @RequestParam(defaultValue = "id") String sort,
                                                                                               @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentSubcontractorResponseDto> pageDocumentSubcontractor = crudDocumentSubcontractor.findAll(pageable);

        return ResponseEntity.ok(pageDocumentSubcontractor);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentSubcontractorResponseDto>> updateDocumentSubcontractor(@RequestBody @Valid DocumentSubcontractorRequestDto documentSubcontractorRequestDto) {
        Optional<DocumentSubcontractorResponseDto> documentSubcontractor = crudDocumentSubcontractor.update(documentSubcontractorRequestDto);

        return ResponseEntity.of(Optional.of(documentSubcontractor));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteDocumentSubcontractor(@PathVariable String id) {
        crudDocumentSubcontractor.delete(id);

        return null;
    }
}

package bl.tech.realiza.gateways.controllers.impl.documents.provider;

import bl.tech.realiza.gateways.controllers.interfaces.documents.provider.DocumentProviderSubcontractorControlller;
import bl.tech.realiza.gateways.requests.documents.provider.DocumentProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.usecases.impl.documents.provider.CrudDocumentProviderSubcontractorImpl;
import bl.tech.realiza.usecases.interfaces.documents.provider.CrudDocumentProviderSubcontractor;
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
public class DocumentProviderSubcontractorControllerImpl implements DocumentProviderSubcontractorControlller {

    private final CrudDocumentProviderSubcontractorImpl crudDocumentSubcontractor;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<DocumentResponseDto> createDocumentProviderSubcontractor(@RequestBody @Valid DocumentProviderSubcontractorRequestDto documentSubcontractorRequestDto) {
        DocumentResponseDto documentSubcontractor = crudDocumentSubcontractor.save(documentSubcontractorRequestDto);

        return ResponseEntity.of(Optional.of(documentSubcontractor));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentResponseDto>> getOneDocumentProviderSubcontractor(@PathVariable String id) {
        Optional<DocumentResponseDto> documentSubcontractor = crudDocumentSubcontractor.findOne(id);

        return ResponseEntity.of(Optional.of(documentSubcontractor));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsProviderSubcontractor(@RequestParam(defaultValue = "0") int page,
                                                                                          @RequestParam(defaultValue = "5") int size,
                                                                                          @RequestParam(defaultValue = "id") String sort,
                                                                                          @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentResponseDto> pageDocumentSubcontractor = crudDocumentSubcontractor.findAll(pageable);

        return ResponseEntity.ok(pageDocumentSubcontractor);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentResponseDto>> updateDocumentProviderSubcontractor(@RequestBody @Valid DocumentProviderSubcontractorRequestDto documentSubcontractorRequestDto) {
        Optional<DocumentResponseDto> documentSubcontractor = crudDocumentSubcontractor.update(documentSubcontractorRequestDto);

        return ResponseEntity.of(Optional.of(documentSubcontractor));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteDocumentProviderSubcontractor(@PathVariable String id) {
        crudDocumentSubcontractor.delete(id);

        return ResponseEntity.noContent().build();
    }
}

package bl.tech.realiza.gateways.controllers.impl.documents.provider;

import bl.tech.realiza.gateways.controllers.interfaces.documents.provider.DocumentProviderSubcontractorControlller;
import bl.tech.realiza.gateways.requests.documents.provider.DocumentProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.usecases.impl.documents.provider.CrudDocumentProviderSubcontractorImpl;
import bl.tech.realiza.usecases.interfaces.documents.provider.CrudDocumentProviderSubcontractor;
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
@RequestMapping("/document/subcontractor")
@Tag(name = "Document Subcontractor")
public class DocumentProviderSubcontractorControllerImpl implements DocumentProviderSubcontractorControlller {

    private final CrudDocumentProviderSubcontractorImpl crudDocumentSubcontractor;

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<DocumentResponseDto> createDocumentProviderSubcontractor(
            @RequestPart("documentSubcontractorRequestDto") @Valid DocumentProviderSubcontractorRequestDto documentSubcontractorRequestDto,
            @RequestParam("file") MultipartFile file) {
        DocumentResponseDto documentSubcontractor = null;
        try {
            documentSubcontractor = crudDocumentSubcontractor.save(documentSubcontractorRequestDto, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
                                                                                          @RequestParam(defaultValue = "idDocumentation") String sort,
                                                                                          @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentResponseDto> pageDocumentSubcontractor = crudDocumentSubcontractor.findAll(pageable);

        return ResponseEntity.ok(pageDocumentSubcontractor);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentResponseDto>> updateDocumentProviderSubcontractor(
            @RequestPart("documentSubcontractorRequestDto")
            @Valid DocumentProviderSubcontractorRequestDto documentSubcontractorRequestDto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        Optional<DocumentResponseDto> documentSubcontractor = null;
        try {
            documentSubcontractor = crudDocumentSubcontractor.update(documentSubcontractorRequestDto, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.of(Optional.of(documentSubcontractor));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteDocumentProviderSubcontractor(@PathVariable String id) {
        crudDocumentSubcontractor.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtered-subcontractor")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsProviderSubcontractorBySubContractor(@RequestParam(defaultValue = "0") int page,
                                                                                                         @RequestParam(defaultValue = "5") int size,
                                                                                                         @RequestParam(defaultValue = "idDocumentation") String sort,
                                                                                                         @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                                                         @RequestParam String idSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentResponseDto> pageDocumentSubcontractor = crudDocumentSubcontractor.findAllBySubcontractor(idSearch, pageable);

        return ResponseEntity.ok(pageDocumentSubcontractor);
    }
}

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
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/document/subcontractor")
@Tag(name = "Document Subcontractor")
public class DocumentProviderSubcontractorControllerImpl implements DocumentProviderSubcontractorControlller {

    private final CrudDocumentProviderSubcontractor crudDocumentSubcontractor;

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<DocumentResponseDto> createDocumentProviderSubcontractor(
            @RequestPart("documentSubcontractorRequestDto") @Valid DocumentProviderSubcontractorRequestDto documentSubcontractorRequestDto) {
        return ResponseEntity.of(Optional.of(crudDocumentSubcontractor.save(documentSubcontractorRequestDto)));
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
                                                                                          @RequestParam(defaultValue = "title") String sort,
                                                                                          @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentResponseDto> pageDocumentSubcontractor = crudDocumentSubcontractor.findAll(pageable);

        return ResponseEntity.ok(pageDocumentSubcontractor);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentResponseDto>> updateDocumentProviderSubcontractor(
            @PathVariable String id,
            @RequestPart("documentSubcontractorRequestDto")
            @Valid DocumentProviderSubcontractorRequestDto documentSubcontractorRequestDto) {
        return ResponseEntity.of(Optional.of(crudDocumentSubcontractor.update(id, documentSubcontractorRequestDto)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteDocumentProviderSubcontractor(@PathVariable String id) {
        crudDocumentSubcontractor.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/upload")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<Optional<DocumentResponseDto>> uploadDocumentProviderSubcontractor(@PathVariable String id,
                                                                              @RequestPart(value = "file") MultipartFile file) {
        Optional<DocumentResponseDto> documentSubcontractor = null;
        try {
            documentSubcontractor = crudDocumentSubcontractor.upload(id, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.of(Optional.of(documentSubcontractor));
    }

    @GetMapping("/filtered-subcontractor")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsProviderSubcontractorBySubContractor(@RequestParam(defaultValue = "0") int page,
                                                                                                         @RequestParam(defaultValue = "5") int size,
                                                                                                         @RequestParam(defaultValue = "title") String sort,
                                                                                                         @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                                                         @RequestParam String idSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentResponseDto> pageDocumentSubcontractor = crudDocumentSubcontractor.findAllBySubcontractor(idSearch, pageable);

        return ResponseEntity.ok(pageDocumentSubcontractor);
    }

    @GetMapping("/{id}/document-matrix")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<DocumentResponseDto> getSubcontractorDocuments(@PathVariable String id) {
        DocumentResponseDto branchResponse = crudDocumentSubcontractor.findAllSelectedDocuments(id);

        return ResponseEntity.ok(branchResponse);
    }

    @PutMapping("/{id}/document-matrix")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<String> updateSubcontractorDocuments(@PathVariable String id, @RequestBody List<String> documentList) {
        String response = crudDocumentSubcontractor.updateRequiredDocuments(id, documentList);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{idEnterprise}/document-matrix")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<String> addRequiredDocument(@PathVariable String idEnterprise, @RequestParam String documentMatrixId) {
        String response = crudDocumentSubcontractor.addRequiredDocument(idEnterprise, documentMatrixId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/document-matrix")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> removeRequiredDocument(@RequestParam String documentId) {
        crudDocumentSubcontractor.removeRequiredDocument(documentId);

        return ResponseEntity.noContent().build();
    }
}

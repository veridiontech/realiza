package bl.tech.realiza.gateways.controllers.impl.documents.provider;

import bl.tech.realiza.gateways.controllers.interfaces.documents.provider.DocumentProviderSupplierControlller;
import bl.tech.realiza.gateways.requests.documents.provider.DocumentProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.usecases.impl.documents.provider.CrudDocumentProviderSupplierImpl;
import bl.tech.realiza.usecases.interfaces.documents.provider.CrudDocumentProviderSupplier;
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
@RequestMapping("/document/supplier")
@Tag(name = "Document Supplier")
public class DocumentProviderSupplierControllerImpl implements DocumentProviderSupplierControlller {

    private final CrudDocumentProviderSupplier crudDocumentSupplier;

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<DocumentResponseDto> createDocumentProviderSupplier(
            @RequestPart("documentSupplierRequestDto") @Valid DocumentProviderSupplierRequestDto documentSupplierRequestDto,
            @RequestParam("file") MultipartFile file) {
        DocumentResponseDto documentSupplier = null;
        try {
            documentSupplier = crudDocumentSupplier.save(documentSupplierRequestDto, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.of(Optional.of(documentSupplier));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentResponseDto>> getOneDocumentProviderSupplier(@PathVariable String id) {
        Optional<DocumentResponseDto> documentSupplier = crudDocumentSupplier.findOne(id);

        return ResponseEntity.of(Optional.of(documentSupplier));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsProviderSupplier(@RequestParam(defaultValue = "0") int page,
                                                                                     @RequestParam(defaultValue = "5") int size,
                                                                                     @RequestParam(defaultValue = "idDocumentation") String sort,
                                                                                     @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentResponseDto> pageDocumentSupplier = crudDocumentSupplier.findAll(pageable);

        return ResponseEntity.ok(pageDocumentSupplier);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentResponseDto>> updateDocumentProviderSupplier(
            @PathVariable String id,
            @RequestPart("documentSupplierRequestDto")
            @Valid DocumentProviderSupplierRequestDto documentSupplierRequestDto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        Optional<DocumentResponseDto> documentSupplier = null;
        try {
            documentSupplier = crudDocumentSupplier.update(id, documentSupplierRequestDto, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.of(Optional.of(documentSupplier));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteDocumentProviderSupplier(@PathVariable String id) {
        crudDocumentSupplier.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/upload")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<Optional<DocumentResponseDto>> uploadDocumentProviderSupplier(@PathVariable String id,
                                                                              @RequestPart(value = "file") MultipartFile file) {
        Optional<DocumentResponseDto> documentSupplier = null;
        try {
            documentSupplier = crudDocumentSupplier.upload(id, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.of(Optional.of(documentSupplier));
    }

    @GetMapping("/filtered-supplier")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsProviderSupplierBySupplier(@RequestParam(defaultValue = "0") int page,
                                                                                               @RequestParam(defaultValue = "5") int size,
                                                                                               @RequestParam(defaultValue = "idDocumentation") String sort,
                                                                                               @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                                               @RequestParam String idSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentResponseDto> pageDocumentSupplier = crudDocumentSupplier.findAllBySupplier(idSearch, pageable);

        return ResponseEntity.ok(pageDocumentSupplier);
    }

    @GetMapping("/{id}/document-matrix")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<DocumentResponseDto> getSupplierDocuments(@PathVariable String id) {
        DocumentResponseDto branchResponse = crudDocumentSupplier.findAllSelectedDocuments(id);

        return ResponseEntity.ok(branchResponse);
    }

    @PutMapping("/{id}/document-matrix")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<String> updateSupplierDocuments(@PathVariable String id, @RequestBody List<String> documentList) {
        String response = crudDocumentSupplier.updateRequiredDocuments(id, documentList);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{idEnterprise}/document-matrix")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<String> addRequiredDocument(@PathVariable String idEnterprise, @RequestParam String documentMatrixId) {
        String response = crudDocumentSupplier.addRequiredDocument(idEnterprise, documentMatrixId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/document-matrix")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> removeRequiredDocument(@RequestParam String documentId) {
        crudDocumentSupplier.removeRequiredDocument(documentId);

        return ResponseEntity.noContent().build();
    }
}

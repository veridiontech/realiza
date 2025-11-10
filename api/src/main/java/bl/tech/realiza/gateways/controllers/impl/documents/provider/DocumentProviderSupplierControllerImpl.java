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
            @RequestPart("documentSupplierRequestDto") @Valid DocumentProviderSupplierRequestDto documentSupplierRequestDto) {
        return ResponseEntity.of(Optional.of(crudDocumentSupplier.save(documentSupplierRequestDto)));
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
                                                                                     @RequestParam(defaultValue = "title") String sort,
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
            @Valid DocumentProviderSupplierRequestDto documentSupplierRequestDto) {
        return ResponseEntity.of(Optional.of(crudDocumentSupplier.update(id, documentSupplierRequestDto)));
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
                                                                                               @RequestParam(defaultValue = "title") String sort,
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

    @GetMapping("/{id}/proxy")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<org.springframework.core.io.Resource> getDocumentProxy(@PathVariable String id) {
        try {
            // Buscar documento na base de dados usando o método existente
            Optional<DocumentResponseDto> documentOpt = crudDocumentSupplier.findOne(id);
            
            if (documentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            DocumentResponseDto document = documentOpt.get();
            
            // Obter signedUrl do Google Cloud Storage
            String signedUrl = document.getSignedUrl();
            
            if (signedUrl == null || signedUrl.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            
            // Fazer download do PDF do Google Cloud Storage
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            byte[] pdfBytes = restTemplate.getForObject(signedUrl, byte[].class);
            
            if (pdfBytes == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            
            // Criar Resource a partir dos bytes
            org.springframework.core.io.ByteArrayResource resource = new org.springframework.core.io.ByteArrayResource(pdfBytes);
            
            // Retornar com headers corretos para visualização inline
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"document.pdf\"")
                    .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .contentLength(pdfBytes.length)
                    .body(resource);
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

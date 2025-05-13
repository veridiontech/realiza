package bl.tech.realiza.gateways.controllers.impl.documents.contract;

import bl.tech.realiza.domains.documents.contract.DocumentContract;
import bl.tech.realiza.gateways.controllers.interfaces.documents.contract.DocumentContractController;
import bl.tech.realiza.gateways.requests.documents.contract.DocumentContractRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.usecases.impl.documents.contract.CrudDocumentContractImpl;
import bl.tech.realiza.usecases.interfaces.documents.contract.CrudDocumentContract;
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
@RequestMapping("/document/contract")
@Tag(name = "Document Contract")
public class DocumentContractControllerImpl implements DocumentContractController {

    private final CrudDocumentContract crudDocumentContract;

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<DocumentResponseDto> createDocumentProviderContract(
            @RequestPart("documentContractRequestDto") @Valid DocumentContractRequestDto documentContractRequestDto,
            @RequestParam("file") MultipartFile file) {
        DocumentResponseDto documentContract = null;
        try {
            documentContract = crudDocumentContract.save(documentContractRequestDto, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.of(Optional.of(documentContract));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentResponseDto>> getOneDocumentProviderContract(@PathVariable String id) {
        Optional<DocumentResponseDto> documentContract = crudDocumentContract.findOne(id);

        return ResponseEntity.of(Optional.of(documentContract));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsProviderContract(@RequestParam(defaultValue = "0") int page,
                                                                                          @RequestParam(defaultValue = "5") int size,
                                                                                          @RequestParam(defaultValue = "idDocumentation") String sort,
                                                                                          @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentResponseDto> pageDocumentContract = crudDocumentContract.findAll(pageable);

        return ResponseEntity.ok(pageDocumentContract);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentResponseDto>> updateDocumentProviderContract(
            @PathVariable String id,
            @RequestPart("documentContractRequestDto")
            @Valid DocumentContractRequestDto documentContractRequestDto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        Optional<DocumentResponseDto> documentContract = null;
        try {
            documentContract = crudDocumentContract.update(id, documentContractRequestDto, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.of(Optional.of(documentContract));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteDocumentProviderContract(@PathVariable String id) {
        crudDocumentContract.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/upload")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<Optional<DocumentResponseDto>> uploadDocumentContract(@PathVariable String id,
                                                                              @RequestPart(value = "file") MultipartFile file) {
        Optional<DocumentResponseDto> documentContract = null;
        try {
            documentContract = crudDocumentContract.upload(id, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.of(Optional.of(documentContract));
    }

    @GetMapping("/filtered-subcontractor")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsProviderContractBySubContractor(@RequestParam(defaultValue = "0") int page,
                                                                                                         @RequestParam(defaultValue = "5") int size,
                                                                                                         @RequestParam(defaultValue = "idDocumentation") String sort,
                                                                                                         @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                                                         @RequestParam String idSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentResponseDto> pageDocumentContract = crudDocumentContract.findAllByContract(idSearch, pageable);

        return ResponseEntity.ok(pageDocumentContract);
    }

    @GetMapping("/{id}/document-matrix")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<DocumentResponseDto> getContractDocuments(@PathVariable String id) {
        DocumentResponseDto branchResponse = crudDocumentContract.findAllSelectedDocuments(id);

        return ResponseEntity.ok(branchResponse);
    }

    @PutMapping("/{id}/document-matrix")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<String> updateContractDocuments(@PathVariable String id, @RequestBody List<String> documentList) {
        String response = crudDocumentContract.updateRequiredDocuments(id, documentList);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{idContract}/document-matrix")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<String> addRequiredDocument(@PathVariable String idContract, @RequestParam String documentMatrixId) {
        String response = crudDocumentContract.addRequiredDocument(documentMatrixId, idContract);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/document-matrix")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> removeRequiredDocument(@RequestParam String documentId) {
        crudDocumentContract.removeRequiredDocument(documentId);

        return ResponseEntity.noContent().build();
    }
}

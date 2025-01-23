package bl.tech.realiza.gateways.controllers.impl.documents.client;

import bl.tech.realiza.gateways.controllers.interfaces.documents.client.DocumentBranchControlller;
import bl.tech.realiza.gateways.requests.documents.client.DocumentBranchRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.usecases.impl.documents.client.CrudDocumentBranchImpl;
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
@RequestMapping("/document/branch")
@Tag(name = "Document Branch")
public class DocumentBranchControllerImpl implements DocumentBranchControlller {

    private final CrudDocumentBranchImpl crudDocumentBranch;

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<DocumentResponseDto> createDocumentBranch(
            @RequestPart("documentBranchRequestDto") @Valid DocumentBranchRequestDto documentBranchRequestDto,
            @RequestPart(value = "file") MultipartFile file) {
        DocumentResponseDto documentBranch = null;
        try {
            documentBranch = crudDocumentBranch.save(documentBranchRequestDto, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.of(Optional.of(documentBranch));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentResponseDto>> getOneDocumentBranch(@PathVariable String id) {
        Optional<DocumentResponseDto> documentBranch = crudDocumentBranch.findOne(id);

        return ResponseEntity.of(Optional.of(documentBranch));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsBranch(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "idDocumentation") String sort,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentResponseDto> pageDocumentBranch = crudDocumentBranch.findAll(pageable);

        return ResponseEntity.ok(pageDocumentBranch);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentResponseDto>> updateDocumentBranch(
            @PathVariable String id,
            @RequestPart("documentBranchRequestDto")
            @Valid DocumentBranchRequestDto documentBranchRequestDto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        Optional<DocumentResponseDto> documentBranch = null;
        try {
            documentBranch = crudDocumentBranch.update(id, documentBranchRequestDto, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.of(Optional.of(documentBranch));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteDocumentBranch(@PathVariable String id) {
        crudDocumentBranch.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtered-branch")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsBranchByBranch(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "idDocumentation") String sort,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam String idSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentResponseDto> pageDocumentBranch = crudDocumentBranch.findAllByBranch(idSearch, pageable);

        return ResponseEntity.ok(pageDocumentBranch);
    }
}

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

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/document/branch")
@Tag(name = "Document Branch")
public class DocumentBranchControllerImpl implements DocumentBranchControlller {

    private final CrudDocumentBranchImpl crudDocumentBranch;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<DocumentResponseDto> createDocumentBranch(@RequestBody @Valid DocumentBranchRequestDto documentBranchRequestDto) {
        DocumentResponseDto documentBranch = crudDocumentBranch.save(documentBranchRequestDto);

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
    public ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsBranch(@RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "5") int size,
                                                                           @RequestParam(defaultValue = "idDocumentation") String sort,
                                                                           @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentResponseDto> pageDocumentBranch = crudDocumentBranch.findAll(pageable);

        return ResponseEntity.ok(pageDocumentBranch);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentResponseDto>> updateDocumentBranch(@RequestBody @Valid DocumentBranchRequestDto documentBranchRequestDto) {
        Optional<DocumentResponseDto> documentBranch = crudDocumentBranch.update(documentBranchRequestDto);

        return ResponseEntity.of(Optional.of(documentBranch));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteDocumentBranch(@PathVariable String id) {
        crudDocumentBranch.delete(id);

        return ResponseEntity.noContent().build();
    }
}

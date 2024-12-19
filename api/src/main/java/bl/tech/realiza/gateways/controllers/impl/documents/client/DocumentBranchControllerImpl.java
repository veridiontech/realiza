package bl.tech.realiza.gateways.controllers.impl.documents.client;

import bl.tech.realiza.gateways.controllers.interfaces.documents.client.DocumentBranchController;
import bl.tech.realiza.gateways.requests.documents.client.DocumentBranchRequestDto;
import bl.tech.realiza.gateways.responses.documents.client.DocumentBranchResponseDto;
import bl.tech.realiza.usecases.impl.documents.client.CrudDocumentBranchImpl;
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
public class DocumentBranchControllerImpl implements DocumentBranchController {

    private final CrudDocumentBranchImpl crudDocumentBranch;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<DocumentBranchResponseDto> createDocumentBranch(@RequestBody @Valid DocumentBranchRequestDto documentBranchRequestDto) {
        DocumentBranchResponseDto documentBranch = crudDocumentBranch.save(documentBranchRequestDto);

        return ResponseEntity.of(Optional.of(documentBranch));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentBranchResponseDto>> getOneDocumentBranch(@PathVariable String id) {
        Optional<DocumentBranchResponseDto> documentBranch = crudDocumentBranch.findOne(id);

        return ResponseEntity.of(Optional.of(documentBranch));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentBranchResponseDto>> getAllDocumentsBranch(@RequestParam(defaultValue = "0") int page,
                                                                                 @RequestParam(defaultValue = "5") int size,
                                                                                 @RequestParam(defaultValue = "id") String sort,
                                                                                 @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentBranchResponseDto> pageDocumentBranch = crudDocumentBranch.findAll(pageable);

        return ResponseEntity.ok(pageDocumentBranch);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentBranchResponseDto>> updateDocumentBranch(@RequestBody @Valid DocumentBranchRequestDto documentBranchRequestDto) {
        Optional<DocumentBranchResponseDto> documentBranch = crudDocumentBranch.update(documentBranchRequestDto);

        return ResponseEntity.of(Optional.of(documentBranch));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteDocumentBranch(@PathVariable String id) {
        crudDocumentBranch.delete(id);

        return null;
    }
}

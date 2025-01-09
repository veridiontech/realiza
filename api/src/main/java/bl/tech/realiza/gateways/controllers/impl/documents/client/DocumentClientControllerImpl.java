package bl.tech.realiza.gateways.controllers.impl.documents.client;

import bl.tech.realiza.gateways.controllers.interfaces.documents.client.DocumentClientControlller;
import bl.tech.realiza.gateways.requests.documents.client.DocumentClientRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.usecases.impl.documents.client.CrudDocumentClientImpl;
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
@RequestMapping("/document/client")
public class DocumentClientControllerImpl implements DocumentClientControlller {

    private final CrudDocumentClientImpl crudDocumentClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<DocumentResponseDto> createDocumentClient(@RequestBody @Valid DocumentClientRequestDto documentClientRequestDto) {
        DocumentResponseDto documentClient = crudDocumentClient.save(documentClientRequestDto);

        return ResponseEntity.of(Optional.of(documentClient));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentResponseDto>> getOneDocumentClient(@PathVariable String id) {
        Optional<DocumentResponseDto> documentClient = crudDocumentClient.findOne(id);

        return ResponseEntity.of(Optional.of(documentClient));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsClient(@RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "5") int size,
                                                                           @RequestParam(defaultValue = "id") String sort,
                                                                           @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentResponseDto> pageDocumentClient = crudDocumentClient.findAll(pageable);

        return ResponseEntity.ok(pageDocumentClient);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentResponseDto>> updateDocumentClient(@RequestBody @Valid DocumentClientRequestDto documentClientRequestDto) {
        Optional<DocumentResponseDto> documentClient = crudDocumentClient.update(documentClientRequestDto);

        return ResponseEntity.of(Optional.of(documentClient));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteDocumentClient(@PathVariable String id) {
        crudDocumentClient.delete(id);

        return ResponseEntity.noContent().build();
    }
}

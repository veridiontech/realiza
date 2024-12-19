package bl.tech.realiza.gateways.controllers.impl.documents.client;

import bl.tech.realiza.gateways.controllers.interfaces.documents.client.DocumentClientController;
import bl.tech.realiza.gateways.requests.documents.client.DocumentClientRequestDto;
import bl.tech.realiza.gateways.responses.documents.client.DocumentClientResponseDto;
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
public class DocumentClientControllerImpl implements DocumentClientController {

    private final CrudDocumentClientImpl crudDocumentClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<DocumentClientResponseDto> createDocumentClient(@RequestBody @Valid DocumentClientRequestDto documentClientRequestDto) {
        DocumentClientResponseDto documentClient = crudDocumentClient.save(documentClientRequestDto);

        return ResponseEntity.of(Optional.of(documentClient));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentClientResponseDto>> getOneDocumentClient(@PathVariable String id) {
        Optional<DocumentClientResponseDto> documentClient = crudDocumentClient.findOne(id);

        return ResponseEntity.of(Optional.of(documentClient));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentClientResponseDto>> getAllDocumentsClient(@RequestParam(defaultValue = "0") int page,
                                                                                 @RequestParam(defaultValue = "5") int size,
                                                                                 @RequestParam(defaultValue = "id") String sort,
                                                                                 @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentClientResponseDto> pageDocumentClient = crudDocumentClient.findAll(pageable);

        return ResponseEntity.ok(pageDocumentClient);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentClientResponseDto>> updateDocumentClient(@RequestBody @Valid DocumentClientRequestDto documentClientRequestDto) {
        Optional<DocumentClientResponseDto> documentClient = crudDocumentClient.update(documentClientRequestDto);

        return ResponseEntity.of(Optional.of(documentClient));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteDocumentClient(@PathVariable String id) {
        crudDocumentClient.delete(id);

        return null;
    }
}

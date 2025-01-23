package bl.tech.realiza.gateways.controllers.impl.documents.client;

import bl.tech.realiza.gateways.controllers.interfaces.documents.client.DocumentClientControlller;
import bl.tech.realiza.gateways.requests.documents.client.DocumentClientRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.usecases.impl.documents.client.CrudDocumentClientImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/document/client")
@Tag(name = "Document Client")
public class DocumentClientControllerImpl implements DocumentClientControlller {

    private final CrudDocumentClientImpl crudDocumentClient;

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<DocumentResponseDto> createDocumentClient(
            @RequestPart("documentClientRequestDto")
            @Valid DocumentClientRequestDto documentClientRequestDto,
            @RequestPart(value = "file")
            MultipartFile file) {
        DocumentResponseDto documentClient = null;

        try {
            documentClient = crudDocumentClient.save(documentClientRequestDto, file);
        } catch (IOException e) {
            throw new RuntimeException("Error saving document client", e);
        }

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
                                                                           @RequestParam(defaultValue = "idDocumentation") String sort,
                                                                           @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentResponseDto> pageDocumentClient = crudDocumentClient.findAll(pageable);

        return ResponseEntity.ok(pageDocumentClient);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentResponseDto>> updateDocumentClient(
            @PathVariable String id,
            @RequestPart("documentClientRequestDto")
            @Valid DocumentClientRequestDto documentClientRequestDto,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        Optional<DocumentResponseDto> documentClient = null;
        try {
            documentClient = crudDocumentClient.update(id, documentClientRequestDto, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.of(Optional.of(documentClient));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteDocumentClient(@PathVariable String id) {
        crudDocumentClient.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtered-client")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsClientByClient(@RequestParam(defaultValue = "0") int page,
                                                                                   @RequestParam(defaultValue = "5") int size,
                                                                                   @RequestParam(defaultValue = "idDocumentation") String sort,
                                                                                   @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                                   @RequestParam String idSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentResponseDto> pageDocumentClient = crudDocumentClient.findAllByClient(idSearch, pageable);

        return ResponseEntity.ok(pageDocumentClient);
    }
}

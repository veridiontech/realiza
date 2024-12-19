package bl.tech.realiza.gateways.controllers.impl.documents.providers;

import bl.tech.realiza.gateways.controllers.interfaces.documents.providers.DocumentSupplierController;
import bl.tech.realiza.gateways.requests.documents.providers.DocumentSupplierRequestDto;
import bl.tech.realiza.gateways.responses.documents.providers.DocumentSupplierResponseDto;
import bl.tech.realiza.usecases.impl.documents.providers.CrudDocumentSupplierImpl;
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
@RequestMapping("/document/supplier")
public class DocumentSupplierControllerImpl implements DocumentSupplierController {

    private final CrudDocumentSupplierImpl crudDocumentSupplier;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<DocumentSupplierResponseDto> createDocumentSupplier(@RequestBody @Valid DocumentSupplierRequestDto documentSupplierRequestDto) {
        DocumentSupplierResponseDto documentSupplier = crudDocumentSupplier.save(documentSupplierRequestDto);

        return ResponseEntity.of(Optional.of(documentSupplier));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentSupplierResponseDto>> getOneDocumentSupplier(@PathVariable String id) {
        Optional<DocumentSupplierResponseDto> documentSupplier = crudDocumentSupplier.findOne(id);

        return ResponseEntity.of(Optional.of(documentSupplier));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentSupplierResponseDto>> getAllDocumentsSupplier(@RequestParam(defaultValue = "0") int page,
                                                                                     @RequestParam(defaultValue = "5") int size,
                                                                                     @RequestParam(defaultValue = "id") String sort,
                                                                                     @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<DocumentSupplierResponseDto> pageDocumentSupplier = crudDocumentSupplier.findAll(pageable);

        return ResponseEntity.ok(pageDocumentSupplier);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentSupplierResponseDto>> updateDocumentSupplier(@RequestBody @Valid DocumentSupplierRequestDto documentSupplierRequestDto) {
        Optional<DocumentSupplierResponseDto> documentSupplier = crudDocumentSupplier.update(documentSupplierRequestDto);

        return ResponseEntity.of(Optional.of(documentSupplier));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteDocumentSupplier(@PathVariable String id) {
        crudDocumentSupplier.delete(id);

        return null;
    }
}

package bl.tech.realiza.gateways.controllers.impl.documents.providers;

import bl.tech.realiza.gateways.controllers.interfaces.documents.providers.DocumentSupplierController;
import bl.tech.realiza.gateways.requests.documents.providers.DocumentSupplierRequestDto;
import bl.tech.realiza.gateways.responses.documents.providers.DocumentSupplierResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/document/supplier")
public class DocumentSupplierControllerImpl implements DocumentSupplierController {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<DocumentSupplierResponseDto> createDocumentSupplier(DocumentSupplierRequestDto documentSupplierRequestDto) {
        return null;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentSupplierResponseDto>> getOneDocumentSupplier(String id) {
        return null;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentSupplierResponseDto>> getAllDocumentsSupplier(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentSupplierResponseDto>> updateDocumentSupplier(DocumentSupplierRequestDto documentSupplierRequestDto) {
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteDocumentSupplier(String id) {
        return null;
    }
}

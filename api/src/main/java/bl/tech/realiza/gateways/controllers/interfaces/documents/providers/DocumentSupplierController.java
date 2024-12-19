package bl.tech.realiza.gateways.controllers.interfaces.documents.providers;

import bl.tech.realiza.gateways.requests.documents.providers.DocumentSupplierRequestDto;
import bl.tech.realiza.gateways.responses.documents.providers.DocumentSupplierResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface DocumentSupplierController {
    ResponseEntity<DocumentSupplierResponseDto> createDocumentSupplier(DocumentSupplierRequestDto documentSupplierRequestDto);
    ResponseEntity<Optional<DocumentSupplierResponseDto>> getOneDocumentSupplier(String id);
    ResponseEntity<Page<DocumentSupplierResponseDto>> getAllDocumentsSupplier(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<DocumentSupplierResponseDto>> updateDocumentSupplier(DocumentSupplierRequestDto documentSupplierRequestDto);
    ResponseEntity<Void> deleteDocumentSupplier(String id);
}

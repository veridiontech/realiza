package bl.tech.realiza.gateways.controllers.interfaces.documents.providers;

import bl.tech.realiza.gateways.requests.documents.providers.DocumentsSupplierRequestDto;
import bl.tech.realiza.gateways.responses.documents.providers.DocumentsSupplierResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface DocumentsSupplierController {
    ResponseEntity<DocumentsSupplierResponseDto> createDocumentSupplier(DocumentsSupplierRequestDto documentsSupplierRequestDto);
    ResponseEntity<Optional<DocumentsSupplierResponseDto>> getOneDocumentSupplier(String id);
    ResponseEntity<Page<DocumentsSupplierResponseDto>> getAllDocumentsSupplier(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<DocumentsSupplierResponseDto>> updateDocumentSupplier(DocumentsSupplierRequestDto documentsSupplierRequestDto);
    ResponseEntity<Void> deleteDocumentSupplier(String id);
}

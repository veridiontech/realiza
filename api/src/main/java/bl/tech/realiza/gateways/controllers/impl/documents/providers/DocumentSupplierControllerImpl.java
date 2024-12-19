package bl.tech.realiza.gateways.controllers.impl.documents.providers;

import bl.tech.realiza.gateways.controllers.interfaces.documents.providers.DocumentSupplierController;
import bl.tech.realiza.gateways.requests.documents.providers.DocumentSupplierRequestDto;
import bl.tech.realiza.gateways.responses.documents.providers.DocumentSupplierResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class DocumentSupplierControllerImpl implements DocumentSupplierController {
    @Override
    public ResponseEntity<DocumentSupplierResponseDto> createDocumentSupplier(DocumentSupplierRequestDto documentSupplierRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<DocumentSupplierResponseDto>> getOneDocumentSupplier(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Page<DocumentSupplierResponseDto>> getAllDocumentsSupplier(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<DocumentSupplierResponseDto>> updateDocumentSupplier(DocumentSupplierRequestDto documentSupplierRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteDocumentSupplier(String id) {
        return null;
    }
}

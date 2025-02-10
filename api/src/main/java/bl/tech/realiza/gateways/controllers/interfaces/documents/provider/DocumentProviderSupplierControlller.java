package bl.tech.realiza.gateways.controllers.interfaces.documents.provider;

import bl.tech.realiza.gateways.requests.documents.provider.DocumentProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface DocumentProviderSupplierControlller {
    ResponseEntity<DocumentResponseDto> createDocumentProviderSupplier(DocumentProviderSupplierRequestDto documentProviderSupplierRequestDto, MultipartFile file);
    ResponseEntity<Optional<DocumentResponseDto>> getOneDocumentProviderSupplier(String id);
    ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsProviderSupplier(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<DocumentResponseDto>> updateDocumentProviderSupplier(String id, DocumentProviderSupplierRequestDto documentProviderSupplierRequestDto, MultipartFile file);
    ResponseEntity<Void> deleteDocumentProviderSupplier(String id);
    ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsProviderSupplierBySupplier(int page, int size, String sort, Sort.Direction direction, String idSearch);
    ResponseEntity<DocumentResponseDto> getSupplierDocuments(String id);
    ResponseEntity<String> updateSupplierDocuments(String id, List<String> documentList);
}

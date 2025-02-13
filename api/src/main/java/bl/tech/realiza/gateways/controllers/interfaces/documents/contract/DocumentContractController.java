package bl.tech.realiza.gateways.controllers.interfaces.documents.contract;

import bl.tech.realiza.gateways.requests.documents.contract.DocumentContractRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface DocumentContractController {
    ResponseEntity<DocumentResponseDto> createDocumentProviderContract(DocumentContractRequestDto documentContractRequestDto, MultipartFile file);
    ResponseEntity<Optional<DocumentResponseDto>> getOneDocumentProviderContract(String id);
    ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsProviderContract(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<DocumentResponseDto>> updateDocumentProviderContract(String id, DocumentContractRequestDto documentContractRequestDto, MultipartFile file);
    ResponseEntity<Void> deleteDocumentProviderContract(String id);
    ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsProviderContractBySubContractor(int page, int size, String sort, Sort.Direction direction, String idSearch);
    ResponseEntity<DocumentResponseDto> getContractDocuments(String id);
    ResponseEntity<String> updateContractDocuments(String id, List<String> documentList);
    ResponseEntity<String> addRequiredDocument(String idEnterprise, String documentMatrixId);
    ResponseEntity<Void> removeRequiredDocument(String documentId);
}

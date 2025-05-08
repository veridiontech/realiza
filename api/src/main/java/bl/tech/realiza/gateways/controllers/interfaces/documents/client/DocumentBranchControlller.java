package bl.tech.realiza.gateways.controllers.interfaces.documents.client;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.gateways.requests.documents.client.DocumentBranchRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentExpirationResponseDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface DocumentBranchControlller {
    ResponseEntity<Optional<DocumentResponseDto>> getOneDocumentBranch(String id);
    ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsBranch(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<DocumentResponseDto>> updateDocumentBranch(String id, DocumentBranchRequestDto documentBranchRequestDto, MultipartFile file);
    ResponseEntity<Optional<DocumentResponseDto>> uploadDocumentBranch(String id, MultipartFile file);
    ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsBranchByBranch(int page, int size, String sort, Sort.Direction direction, String idSearch);
    ResponseEntity<DocumentResponseDto> getBranchDocuments(String id);
    ResponseEntity<String> addRequiredDocument(String idEnterprise, String documentMatrixId);
    ResponseEntity<Void> removeRequiredDocument(String documentId);
    ResponseEntity<List<DocumentResponseDto>> getAllFilteredDocumentBranch(String idBranch, String documentTypeName, Boolean isSelected);
    ResponseEntity<List<DocumentExpirationResponseDto>> getAllFilteredDocumentBranchExpiration(String idBranch, String documentTypeName, Boolean isSelected);
    ResponseEntity<String> updateSelectedBranchDocuments(Boolean isSelected, List<String> documentList);
}

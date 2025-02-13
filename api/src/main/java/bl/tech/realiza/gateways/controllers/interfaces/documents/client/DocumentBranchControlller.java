package bl.tech.realiza.gateways.controllers.interfaces.documents.client;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.gateways.requests.documents.client.DocumentBranchRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface DocumentBranchControlller {
    ResponseEntity<DocumentResponseDto> createDocumentBranch(DocumentBranchRequestDto documentBranchRequestDto, MultipartFile file);
    ResponseEntity<Optional<DocumentResponseDto>> getOneDocumentBranch(String id);
    ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsBranch(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<DocumentResponseDto>> updateDocumentBranch(String id, DocumentBranchRequestDto documentBranchRequestDto, MultipartFile file);
    ResponseEntity<Void> deleteDocumentBranch(String id);
    ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsBranchByBranch(int page, int size, String sort, Sort.Direction direction, String idSearch);
    ResponseEntity<DocumentResponseDto> getBranchDocuments(String id);
    ResponseEntity<String> updateBranchDocuments(String id, List<String> documentList);
    ResponseEntity<String> addRequiredDocument(String idEnterprise, String documentMatrixId);
    ResponseEntity<Void> removeRequiredDocument(String documentId);
    ResponseEntity<String> updateBranchDocumentsRisk(String id, List<DocumentBranch> documentList);
    ResponseEntity<List<DocumentResponseDto>> getDocumentBranchByIdAndRisk(String id, Document.Risk risk);
}

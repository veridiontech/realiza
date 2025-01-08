package bl.tech.realiza.gateways.controllers.interfaces.documents.client;

import bl.tech.realiza.gateways.requests.documents.client.DocumentBranchRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface DocumentBranchControlller {
    ResponseEntity<DocumentResponseDto> createDocumentBranch(DocumentBranchRequestDto documentBranchRequestDto);
    ResponseEntity<Optional<DocumentResponseDto>> getOneDocumentBranch(String id);
    ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsBranch(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<DocumentResponseDto>> updateDocumentBranch(DocumentBranchRequestDto documentBranchRequestDto);
    ResponseEntity<Void> deleteDocumentBranch(String id);
}

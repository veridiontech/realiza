package bl.tech.realiza.gateways.controllers.interfaces.documents.client;

import bl.tech.realiza.gateways.requests.documents.client.DocumentBranchRequestDto;
import bl.tech.realiza.gateways.responses.documents.client.DocumentBranchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface DocumentBranchController {
    ResponseEntity<DocumentBranchResponseDto> createDocumentBranch(DocumentBranchRequestDto documentBranchRequestDto);
    ResponseEntity<Optional<DocumentBranchResponseDto>> getOneDocumentBranch(String id);
    ResponseEntity<Page<DocumentBranchResponseDto>> getAllDocumentsBranch(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<DocumentBranchResponseDto>> updateDocumentBranch(DocumentBranchRequestDto documentBranchRequestDto);
    ResponseEntity<Void> deleteDocumentBranch(String id);
}

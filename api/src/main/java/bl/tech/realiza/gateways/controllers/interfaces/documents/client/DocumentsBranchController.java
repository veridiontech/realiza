package bl.tech.realiza.gateways.controllers.interfaces.documents.client;

import bl.tech.realiza.gateways.requests.documents.client.DocumentsBranchRequestDto;
import bl.tech.realiza.gateways.responses.documents.client.DocumentsBranchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface DocumentsBranchController {
    ResponseEntity<DocumentsBranchResponseDto> createDocumentBranch(DocumentsBranchRequestDto documentsBranchRequestDto);
    ResponseEntity<Optional<DocumentsBranchResponseDto>> getOneDocumentBranch(String id);
    ResponseEntity<Page<DocumentsBranchResponseDto>> getAllDocumentsBranch(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<DocumentsBranchResponseDto>> updateDocumentBranch(DocumentsBranchRequestDto documentsBranchRequestDto);
    ResponseEntity<Void> deleteDocumentBranch(String id);
}

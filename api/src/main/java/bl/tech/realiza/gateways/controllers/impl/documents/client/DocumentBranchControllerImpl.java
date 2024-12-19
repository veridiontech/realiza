package bl.tech.realiza.gateways.controllers.impl.documents.client;

import bl.tech.realiza.gateways.controllers.interfaces.documents.client.DocumentBranchController;
import bl.tech.realiza.gateways.requests.documents.client.DocumentBranchRequestDto;
import bl.tech.realiza.gateways.responses.documents.client.DocumentBranchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class DocumentBranchControllerImpl implements DocumentBranchController {
    @Override
    public ResponseEntity<DocumentBranchResponseDto> createDocumentBranch(DocumentBranchRequestDto documentBranchRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<DocumentBranchResponseDto>> getOneDocumentBranch(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Page<DocumentBranchResponseDto>> getAllDocumentsBranch(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<DocumentBranchResponseDto>> updateDocumentBranch(DocumentBranchRequestDto documentBranchRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteDocumentBranch(String id) {
        return null;
    }
}

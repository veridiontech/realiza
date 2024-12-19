package bl.tech.realiza.gateways.controllers.impl.documents.client;

import bl.tech.realiza.gateways.controllers.interfaces.documents.client.DocumentBranchController;
import bl.tech.realiza.gateways.requests.documents.client.DocumentBranchRequestDto;
import bl.tech.realiza.gateways.responses.documents.client.DocumentBranchResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/document/branch")
public class DocumentBranchControllerImpl implements DocumentBranchController {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<DocumentBranchResponseDto> createDocumentBranch(DocumentBranchRequestDto documentBranchRequestDto) {
        return null;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentBranchResponseDto>> getOneDocumentBranch(String id) {
        return null;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<DocumentBranchResponseDto>> getAllDocumentsBranch(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<DocumentBranchResponseDto>> updateDocumentBranch(DocumentBranchRequestDto documentBranchRequestDto) {
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteDocumentBranch(String id) {
        return null;
    }
}

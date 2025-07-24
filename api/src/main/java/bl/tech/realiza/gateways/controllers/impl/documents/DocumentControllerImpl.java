package bl.tech.realiza.gateways.controllers.impl.documents;

import bl.tech.realiza.gateways.controllers.interfaces.documents.DocumentController;
import bl.tech.realiza.gateways.requests.documents.DocumentStatusChangeRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentPendingResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.document.CrudDocument;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/document")
@Tag(name = "Document")
public class DocumentControllerImpl implements DocumentController {

    private final CrudDocument crudDocument;

    @PostMapping("/{documentId}/change-status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_REALIZA_BASIC')")
    @Override
    public ResponseEntity<String> changeDocumentStatus(@PathVariable String documentId, @RequestBody DocumentStatusChangeRequestDto documentStatusChangeRequest) {
        return ResponseEntity.ok(crudDocument.changeStatus(documentId, documentStatusChangeRequest));
    }

    @PostMapping("/{documentId}/exempt")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_REALIZA_BASIC')")
    @Override
    public ResponseEntity<String> documentExemption(@PathVariable String documentId, @RequestParam String contractId) {
        return ResponseEntity.ok(crudDocument.documentExemption(documentId, contractId));
    }

    @PostMapping("/{documentId}/exempt-request")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<String> documentExemptionRequest(@PathVariable String documentId, @RequestParam String contractId) {
        return ResponseEntity.ok(crudDocument.documentExemptionRequest(documentId, contractId));
    }

    @GetMapping("/non-conforming/{enterpriseId}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<List<DocumentPendingResponseDto>> nonConformingDocumentsByEnterpriseId(@PathVariable String enterpriseId) {
        return ResponseEntity.ok(crudDocument.findNonConformingDocumentByEnterpriseId(enterpriseId));
    }
}

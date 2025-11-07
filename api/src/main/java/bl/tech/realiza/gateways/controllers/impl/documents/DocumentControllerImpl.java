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

    @PutMapping("/{documentId}/change-status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_REALIZA_BASIC')")
    @Override
    public ResponseEntity<Void> changeDocumentStatus(@PathVariable String documentId, @RequestBody DocumentStatusChangeRequestDto documentStatusChangeRequest) {
        crudDocument.changeStatus(documentId, documentStatusChangeRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{documentId}/exempt")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<String> documentExemption(@PathVariable String documentId, @RequestParam String contractId, @RequestParam String description) {
        return ResponseEntity.ok(crudDocument.documentExemptionRequest(documentId, contractId, description));
    }

    @GetMapping("/non-conforming/{enterpriseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<List<DocumentPendingResponseDto>> nonConformingDocumentsByEnterpriseId(@PathVariable String enterpriseId) {
        return ResponseEntity.ok(crudDocument.findNonConformingDocumentByEnterpriseId(enterpriseId));
    }

    @GetMapping("/find-by-audit-log/{auditLogId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<String> getVersionByAuditLogId(@PathVariable String auditLogId) {
        return ResponseEntity.ok(crudDocument.findVersionByAuditLog(auditLogId));
    }

    @PutMapping("/branch/{documentBranchId}/change-status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_REALIZA_BASIC')")
    @Override
    public ResponseEntity<Void> changeDocumentBranchStatus(@PathVariable String documentBranchId, @RequestBody DocumentStatusChangeRequestDto documentStatusChangeRequest) {
        crudDocument.changeDocumentBranchStatus(documentBranchId, documentStatusChangeRequest);
        return ResponseEntity.noContent().build();
    }
}

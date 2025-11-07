package bl.tech.realiza.controllers.documents;

import bl.tech.realiza.gateways.requests.documents.DocumentStatusChangeRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentPendingResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface DocumentController {

    @PutMapping("/document/{documentId}/change-status")
    ResponseEntity<Void> changeDocumentStatus(@PathVariable String documentId, @RequestBody DocumentStatusChangeRequestDto request);

    @PutMapping("/document/branch/{documentBranchId}/change-status")
    ResponseEntity<Void> changeDocumentBranchStatus(@PathVariable String documentBranchId, @RequestBody DocumentStatusChangeRequestDto request);

    @GetMapping("/document/non-conforming/{enterpriseId}")
    ResponseEntity<List<DocumentPendingResponseDto>> findNonConformingDocumentByEnterpriseId(@PathVariable String enterpriseId);

    @PostMapping("/document/exemption-request/{documentId}/{contractId}")
    ResponseEntity<String> documentExemptionRequest(@PathVariable String documentId, @PathVariable String contractId, @RequestParam String description);
}

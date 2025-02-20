package bl.tech.realiza.gateways.controllers.interfaces.documents.provider;

import bl.tech.realiza.gateways.requests.documents.provider.DocumentProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface DocumentProviderSubcontractorControlller {
    ResponseEntity<DocumentResponseDto> createDocumentProviderSubcontractor(DocumentProviderSubcontractorRequestDto documentProviderSubcontractorRequestDto, MultipartFile file);
    ResponseEntity<Optional<DocumentResponseDto>> getOneDocumentProviderSubcontractor(String id);
    ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsProviderSubcontractor(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<DocumentResponseDto>> updateDocumentProviderSubcontractor(String id, DocumentProviderSubcontractorRequestDto documentProviderSubcontractorRequestDto, MultipartFile file);
    ResponseEntity<Void> deleteDocumentProviderSubcontractor(String id);
    ResponseEntity<Optional<DocumentResponseDto>> uploadDocumentProviderSubcontractor(String id, MultipartFile file);
    ResponseEntity<Page<DocumentResponseDto>> getAllDocumentsProviderSubcontractorBySubContractor(int page, int size, String sort, Sort.Direction direction, String idSearch);
    ResponseEntity<DocumentResponseDto> getSubcontractorDocuments(String id);
    ResponseEntity<String> updateSubcontractorDocuments(String id, List<String> documentList);
    ResponseEntity<String> addRequiredDocument(String idEnterprise, String documentMatrixId);
    ResponseEntity<Void> removeRequiredDocument(String documentId);
}

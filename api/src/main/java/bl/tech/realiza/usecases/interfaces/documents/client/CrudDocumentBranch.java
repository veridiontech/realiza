package bl.tech.realiza.usecases.interfaces.documents.client;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.gateways.requests.documents.client.DocumentBranchRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CrudDocumentBranch {
    DocumentResponseDto save(DocumentBranchRequestDto documentBranchRequestDto, MultipartFile file) throws IOException;
    Optional<DocumentResponseDto> findOne(String id);
    Page<DocumentResponseDto> findAll(Pageable pageable);
    Optional<DocumentResponseDto> update(String id, DocumentBranchRequestDto documentBranchRequestDto, MultipartFile file) throws IOException ;
    void delete(String id);
    Optional<DocumentResponseDto> upload(String id, MultipartFile file) throws IOException ;
    Page<DocumentResponseDto> findAllByBranch(String idSearch, Pageable pageable);
    DocumentResponseDto findAllSelectedDocuments (String id);
    String updateRequiredDocumentsByList(String id, List<String> documentCollection);
    String addRequiredDocument(String idEnterprise, String documentMatrixId);
    void removeRequiredDocument(String documentId);
    String updateSelectedDocuments(String id, List<DocumentBranch> documentCollection);
    List<DocumentResponseDto> findAllSelectedDocumentsByRisk(String id, Document.Risk risk);
}

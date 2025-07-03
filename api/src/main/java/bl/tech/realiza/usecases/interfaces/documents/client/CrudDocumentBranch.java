package bl.tech.realiza.usecases.interfaces.documents.client;

import bl.tech.realiza.gateways.requests.documents.client.DocumentBranchRequestDto;
import bl.tech.realiza.gateways.requests.documents.client.DocumentExpirationUpdateRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentExpirationResponseDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.gateways.responses.documents.DocumentSummarizedResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CrudDocumentBranch {
    Optional<DocumentResponseDto> findOne(String id);
    Page<DocumentResponseDto> findAll(Pageable pageable);
    Optional<DocumentResponseDto> update(String id, DocumentBranchRequestDto documentBranchRequestDto, MultipartFile file) throws IOException ;
    Optional<DocumentResponseDto> upload(String id, MultipartFile file) throws IOException ;
    Page<DocumentResponseDto> findAllByBranch(String idSearch, Pageable pageable);
    DocumentResponseDto findAllSelectedDocuments (String id);
    List<DocumentSummarizedResponseDto> findAllFilteredDocuments(String id, String documentTypeName, Boolean isSelected);
    List<DocumentExpirationResponseDto> findAllFilteredDocumentsExpiration(String idBranch, String documentTypeName, Boolean isSelected);
    String updateSelectedDocuments(Boolean isSelected, List<String> documentCollection, Boolean replicate);
    String addRequiredDocument(String idEnterprise, String documentMatrixId);
    void removeRequiredDocument(String documentId);

    DocumentExpirationResponseDto updateSelectedDocumentExpiration(String idDocumentation, DocumentExpirationUpdateRequestDto documentExpirationUpdateRequestDto, Boolean replicate);
}

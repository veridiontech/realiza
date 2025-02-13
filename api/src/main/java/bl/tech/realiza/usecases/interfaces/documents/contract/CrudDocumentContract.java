package bl.tech.realiza.usecases.interfaces.documents.contract;

import bl.tech.realiza.gateways.requests.documents.contract.DocumentContractRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CrudDocumentContract {
    DocumentResponseDto save(DocumentContractRequestDto documentContractRequestDto, MultipartFile file) throws IOException;
    Optional<DocumentResponseDto> findOne(String id);
    Page<DocumentResponseDto> findAll(Pageable pageable);
    Optional<DocumentResponseDto> update(String id, DocumentContractRequestDto documentContractRequestDto, MultipartFile file) throws IOException;
    void delete(String id);
    Page<DocumentResponseDto> findAllByContract(String idSearch, Pageable pageable);
    DocumentResponseDto findAllSelectedDocuments (String id);
    String updateRequiredDocuments(String id, List<String> documentCollection);
    String addRequiredDocument(String idEnterprise, String documentMatrixId);
    void removeRequiredDocument(String documentId);
}

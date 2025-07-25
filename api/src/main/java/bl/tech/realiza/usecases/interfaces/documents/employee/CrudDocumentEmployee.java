package bl.tech.realiza.usecases.interfaces.documents.employee;

import bl.tech.realiza.gateways.requests.documents.employee.DocumentEmployeeRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CrudDocumentEmployee {
    DocumentResponseDto save(DocumentEmployeeRequestDto documentEmployeeRequestDto);
    Optional<DocumentResponseDto> findOne(String id);
    Page<DocumentResponseDto> findAll(Pageable pageable);
    Optional<DocumentResponseDto> update(String id, DocumentEmployeeRequestDto documentEmployeeRequestDto);
    void delete(String id);
    Optional<DocumentResponseDto> upload(String id, MultipartFile file) throws IOException ;
    Page<DocumentResponseDto> findAllByEmployee(String idSearch, Pageable pageable);
    DocumentResponseDto findAllSelectedDocuments (String id);
    String updateRequiredDocuments(String id, List<String> documentCollection);
    String solicitNewRequiredDocument(String id, String documentId);
    String addRequiredDocument(String idEnterprise, String documentMatrixId);
    void removeRequiredDocument(String documentId);
}

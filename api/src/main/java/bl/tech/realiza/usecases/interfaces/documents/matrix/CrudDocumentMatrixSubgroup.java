//package bl.tech.realiza.usecases.interfaces.documents.matrix;
//
//import bl.tech.realiza.gateways.requests.documents.employee.DocumentEmployeeRequestDto;
//import bl.tech.realiza.gateways.requests.documents.matrix.DocumentMatrixRequestDto;
//import bl.tech.realiza.gateways.requests.documents.matrix.DocumentMatrixSubgroupRequestDto;
//import bl.tech.realiza.gateways.responses.documents.DocumentMatrixResponseDto;
//import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.Optional;
//
//public interface CrudDocumentMatrixSubgroup {
//    DocumentMatrixResponseDto save(DocumentMatrixSubgroupRequestDto documentMatrixSubgroupRequest);
//    Optional<DocumentMatrixResponseDto> findOne(String id);
//    Page<DocumentMatrixResponseDto> findAll(Pageable pageable);
//    Optional<DocumentMatrixResponseDto> update(String id, DocumentMatrixSubgroupRequestDto documentMatrixSubgroupRequest);
//    void delete(String id);
//    Page<DocumentMatrixResponseDto> findAllByGroup(String idSearch, Pageable pageable);
//}

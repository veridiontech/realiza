//package bl.tech.realiza.gateways.controllers.interfaces.documents.matrix;
//
//import bl.tech.realiza.gateways.requests.documents.matrix.DocumentMatrixSubgroupRequestDto;
//import bl.tech.realiza.gateways.responses.documents.DocumentMatrixResponseDto;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.ResponseEntity;
//
//import java.util.Optional;
//
//public interface DocumentMatrixSubgroupController {
//    ResponseEntity<DocumentMatrixResponseDto> createDocumentMatrixSubgroup(DocumentMatrixSubgroupRequestDto documentMatrixSubgroupRequestDto);
//    ResponseEntity<Optional<DocumentMatrixResponseDto>> getOneDocumentMatrixSubgroup(String id);
//    ResponseEntity<Page<DocumentMatrixResponseDto>> getAllDocumentsMatrixSubgroup(int page, int size, String sort, Sort.Direction direction);
//    ResponseEntity<Optional<DocumentMatrixResponseDto>> updateDocumentMatrixSubgroup(String id, DocumentMatrixSubgroupRequestDto documentMatrixSubgroupRequestDto);
//    ResponseEntity<Void> deleteDocumentMatrixSubgroup(String id);
//    ResponseEntity<Page<DocumentMatrixResponseDto>> getAllDocumentsMatrixSubgroupByGroup(int page, int size, String sort, Sort.Direction direction, String idSearch);
//}

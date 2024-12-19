package bl.tech.realiza.usecases.interfaces.documents.employee;

import bl.tech.realiza.gateways.requests.documents.employee.DocumentsEmployeeRequestDto;
import bl.tech.realiza.gateways.responses.documents.employee.DocumentsEmployeeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudDocumentsEmployee {
    DocumentsEmployeeResponseDto save(DocumentsEmployeeRequestDto documentsEmployeeRequestDto);
    Optional<DocumentsEmployeeResponseDto> findOne(String id);
    Page<DocumentsEmployeeResponseDto> findAll(Pageable pageable);
    Optional<DocumentsEmployeeResponseDto> update(DocumentsEmployeeRequestDto documentsEmployeeRequestDto);
    void delete(String id);
}

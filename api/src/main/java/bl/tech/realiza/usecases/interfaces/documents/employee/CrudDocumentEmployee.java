package bl.tech.realiza.usecases.interfaces.documents.employee;

import bl.tech.realiza.gateways.requests.documents.employee.DocumentEmployeeRequestDto;
import bl.tech.realiza.gateways.responses.documents.employee.DocumentEmployeeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudDocumentEmployee {
    DocumentEmployeeResponseDto save(DocumentEmployeeRequestDto documentEmployeeRequestDto);
    Optional<DocumentEmployeeResponseDto> findOne(String id);
    Page<DocumentEmployeeResponseDto> findAll(Pageable pageable);
    Optional<DocumentEmployeeResponseDto> update(DocumentEmployeeRequestDto documentEmployeeRequestDto);
    void delete(String id);
}

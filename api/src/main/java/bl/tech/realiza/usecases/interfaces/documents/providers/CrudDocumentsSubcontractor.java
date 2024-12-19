package bl.tech.realiza.usecases.interfaces.documents.providers;

import bl.tech.realiza.gateways.requests.documents.employee.DocumentsEmployeeRequestDto;
import bl.tech.realiza.gateways.requests.documents.providers.DocumentsSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.documents.employee.DocumentsEmployeeResponseDto;
import bl.tech.realiza.gateways.responses.documents.providers.DocumentsSubcontractorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudDocumentsSubcontractor {
    DocumentsSubcontractorResponseDto save(DocumentsSubcontractorRequestDto documentsSubcontractorRequestDto);
    Optional<DocumentsSubcontractorResponseDto> findOne(String id);
    Page<DocumentsSubcontractorResponseDto> findAll(Pageable pageable);
    Optional<DocumentsSubcontractorResponseDto> update(DocumentsSubcontractorRequestDto documentsSubcontractorRequestDto);
    void delete(String id);
}

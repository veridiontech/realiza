package bl.tech.realiza.usecases.interfaces.records.denied;

import bl.tech.realiza.gateways.requests.records.denied.DocumentRecordEmployeeRequestDto;
import bl.tech.realiza.gateways.responses.records.denied.DocumentRecordEmployeeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CrudDocumentRecordEmployee {
    DocumentRecordEmployeeResponseDto save(DocumentRecordEmployeeRequestDto documentRecordEmployeeRequestDto);
    Page<DocumentRecordEmployeeResponseDto> findAllByDocument(Pageable pageable);
    void deleteAllByDocument(String id);
}

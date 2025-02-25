package bl.tech.realiza.usecases.interfaces.records.denied;

import bl.tech.realiza.gateways.requests.records.denied.DocumentRecordSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.records.denied.DocumentRecordSubcontractorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CrudDocumentRecordSubcontractor {
    DocumentRecordSubcontractorResponseDto save(DocumentRecordSubcontractorRequestDto documentRecordSubcontractorRequestDto);
    Page<DocumentRecordSubcontractorResponseDto> findAllByDocument(Pageable pageable);
    void deleteAllByDocument(String id);
}

package bl.tech.realiza.usecases.interfaces.records.denied;

import bl.tech.realiza.gateways.requests.records.denied.DocumentRecordClientRequestDto;
import bl.tech.realiza.gateways.responses.records.denied.DocumentRecordClientResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CrudDocumentRecordClient {
    DocumentRecordClientResponseDto save(DocumentRecordClientRequestDto documentRecordClientRequestDto);
    Page<DocumentRecordClientResponseDto> findAllByDocument(Pageable pageable);
    void deleteAllByDocument(String id);
}

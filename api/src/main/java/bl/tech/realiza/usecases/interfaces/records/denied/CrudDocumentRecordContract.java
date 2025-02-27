package bl.tech.realiza.usecases.interfaces.records.denied;

import bl.tech.realiza.gateways.requests.records.denied.DocumentRecordContractRequestDto;
import bl.tech.realiza.gateways.responses.records.denied.DocumentRecordContractResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CrudDocumentRecordContract {
    DocumentRecordContractResponseDto save(DocumentRecordContractRequestDto documentRecordContractRequestDto);
    Page<DocumentRecordContractResponseDto> findAllByDocument(Pageable pageable);
    void deleteAllByDocument(String id);
}

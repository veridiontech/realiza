package bl.tech.realiza.usecases.interfaces.records.denied;

import bl.tech.realiza.gateways.requests.records.denied.DocumentRecordSupplierRequestDto;
import bl.tech.realiza.gateways.responses.records.denied.DocumentRecordSupplierResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CrudDocumentRecordSupplier {
    DocumentRecordSupplierResponseDto save(DocumentRecordSupplierRequestDto documentRecordSupplierRequestDto);
    Page<DocumentRecordSupplierResponseDto> findAllByDocument(Pageable pageable);
    void deleteAllByDocument(String id);
}

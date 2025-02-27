package bl.tech.realiza.usecases.impl.records.denied;

import bl.tech.realiza.gateways.requests.records.denied.DocumentRecordSupplierRequestDto;
import bl.tech.realiza.gateways.responses.records.denied.DocumentRecordSupplierResponseDto;
import bl.tech.realiza.usecases.interfaces.records.denied.CrudDocumentRecordSupplier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrudDocumentRecordSupplierImpl implements CrudDocumentRecordSupplier {
    @Override
    public DocumentRecordSupplierResponseDto save(DocumentRecordSupplierRequestDto documentRecordSupplierRequestDto) {
        return null;
    }

    @Override
    public Page<DocumentRecordSupplierResponseDto> findAllByDocument(Pageable pageable) {
        return null;
    }

    @Override
    public void deleteAllByDocument(String id) {

    }
}

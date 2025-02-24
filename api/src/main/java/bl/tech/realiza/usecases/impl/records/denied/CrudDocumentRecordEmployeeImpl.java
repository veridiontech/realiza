package bl.tech.realiza.usecases.impl.records.denied;

import bl.tech.realiza.gateways.requests.records.denied.DocumentRecordEmployeeRequestDto;
import bl.tech.realiza.gateways.responses.records.denied.DocumentRecordEmployeeResponseDto;
import bl.tech.realiza.usecases.interfaces.records.denied.CrudDocumentRecordEmployee;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrudDocumentRecordEmployeeImpl implements CrudDocumentRecordEmployee {
    @Override
    public DocumentRecordEmployeeResponseDto save(DocumentRecordEmployeeRequestDto documentRecordEmployeeRequestDto) {
        return null;
    }

    @Override
    public Page<DocumentRecordEmployeeResponseDto> findAllByDocument(Pageable pageable) {
        return null;
    }

    @Override
    public void deleteAllByDocument(String id) {

    }
}

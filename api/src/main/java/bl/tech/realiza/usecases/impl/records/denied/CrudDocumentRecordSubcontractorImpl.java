package bl.tech.realiza.usecases.impl.records.denied;

import bl.tech.realiza.gateways.requests.records.denied.DocumentRecordSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.records.denied.DocumentRecordSubcontractorResponseDto;
import bl.tech.realiza.usecases.interfaces.records.denied.CrudDocumentRecordSubcontractor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrudDocumentRecordSubcontractorImpl implements CrudDocumentRecordSubcontractor {
    @Override
    public DocumentRecordSubcontractorResponseDto save(DocumentRecordSubcontractorRequestDto documentRecordSubcontractorRequestDto) {
        return null;
    }

    @Override
    public Page<DocumentRecordSubcontractorResponseDto> findAllByDocument(Pageable pageable) {
        return null;
    }

    @Override
    public void deleteAllByDocument(String id) {

    }
}

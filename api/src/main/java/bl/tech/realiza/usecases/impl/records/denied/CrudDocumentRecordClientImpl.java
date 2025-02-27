package bl.tech.realiza.usecases.impl.records.denied;

import bl.tech.realiza.gateways.requests.records.denied.DocumentRecordClientRequestDto;
import bl.tech.realiza.gateways.responses.records.denied.DocumentRecordClientResponseDto;
import bl.tech.realiza.usecases.interfaces.records.denied.CrudDocumentRecordClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrudDocumentRecordClientImpl implements CrudDocumentRecordClient {
    @Override
    public DocumentRecordClientResponseDto save(DocumentRecordClientRequestDto documentRecordClientRequestDto) {
        return null;
    }

    @Override
    public Page<DocumentRecordClientResponseDto> findAllByDocument(Pageable pageable) {
        return null;
    }

    @Override
    public void deleteAllByDocument(String id) {

    }
}

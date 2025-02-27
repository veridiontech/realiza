package bl.tech.realiza.usecases.impl.records.denied;

import bl.tech.realiza.gateways.requests.records.denied.DocumentRecordContractRequestDto;
import bl.tech.realiza.gateways.responses.records.denied.DocumentRecordContractResponseDto;
import bl.tech.realiza.usecases.interfaces.records.denied.CrudDocumentRecordContract;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrudDocumentRecordContractImpl implements CrudDocumentRecordContract {
    @Override
    public DocumentRecordContractResponseDto save(DocumentRecordContractRequestDto documentRecordContractRequestDto) {
        return null;
    }

    @Override
    public Page<DocumentRecordContractResponseDto> findAllByDocument(Pageable pageable) {
        return null;
    }

    @Override
    public void deleteAllByDocument(String id) {

    }
}

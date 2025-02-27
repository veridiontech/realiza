package bl.tech.realiza.usecases.impl.records.denied;

import bl.tech.realiza.gateways.requests.records.denied.DocumentRecordBranchRequestDto;
import bl.tech.realiza.gateways.responses.records.denied.DocumentRecordBranchResponseDto;
import bl.tech.realiza.usecases.interfaces.records.denied.CrudDocumentRecordBranch;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrudDocumentRecordBranchImpl implements CrudDocumentRecordBranch {
    @Override
    public DocumentRecordBranchResponseDto save(DocumentRecordBranchRequestDto documentRecordBranchRequestDto) {
        return null;
    }

    @Override
    public Page<DocumentRecordBranchResponseDto> findAllByDocument(Pageable pageable) {
        return null;
    }

    @Override
    public void deleteAllByDocument(String id) {

    }
}

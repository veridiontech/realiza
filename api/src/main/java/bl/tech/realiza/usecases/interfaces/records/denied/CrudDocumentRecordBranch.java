package bl.tech.realiza.usecases.interfaces.records.denied;

import bl.tech.realiza.gateways.requests.records.denied.DocumentRecordBranchRequestDto;
import bl.tech.realiza.gateways.requests.services.ContactRequestDto;
import bl.tech.realiza.gateways.responses.records.denied.DocumentRecordBranchResponseDto;
import bl.tech.realiza.gateways.responses.services.ContactResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudDocumentRecordBranch {
    DocumentRecordBranchResponseDto save(DocumentRecordBranchRequestDto documentRecordBranchRequestDto);
    Page<DocumentRecordBranchResponseDto> findAllByDocument(Pageable pageable);
    void deleteAllByDocument(String id);
}

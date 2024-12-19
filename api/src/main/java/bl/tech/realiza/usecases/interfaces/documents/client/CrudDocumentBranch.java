package bl.tech.realiza.usecases.interfaces.documents.client;

import bl.tech.realiza.gateways.requests.documents.client.DocumentBranchRequestDto;
import bl.tech.realiza.gateways.responses.documents.client.DocumentBranchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudDocumentBranch {
    DocumentBranchResponseDto save(DocumentBranchRequestDto documentBranchRequestDto);
    Optional<DocumentBranchResponseDto> findOne(String id);
    Page<DocumentBranchResponseDto> findAll(Pageable pageable);
    Optional<DocumentBranchResponseDto> update(DocumentBranchRequestDto documentBranchRequestDto);
    void delete(String id);
}

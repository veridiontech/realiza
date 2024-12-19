package bl.tech.realiza.usecases.interfaces.documents.client;

import bl.tech.realiza.gateways.requests.documents.client.DocumentsBranchRequestDto;
import bl.tech.realiza.gateways.responses.documents.client.DocumentsBranchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudDocumentsBranch {
    DocumentsBranchResponseDto save(DocumentsBranchRequestDto documentsBranchRequestDto);
    Optional<DocumentsBranchResponseDto> findOne(String id);
    Page<DocumentsBranchResponseDto> findAll(Pageable pageable);
    Optional<DocumentsBranchResponseDto> update(String id, DocumentsBranchRequestDto documentsBranchRequestDto);
    void delete(String id);
}

package bl.tech.realiza.usecases.impl.documents.client;

import bl.tech.realiza.gateways.requests.documents.client.DocumentBranchRequestDto;
import bl.tech.realiza.gateways.responses.documents.client.DocumentBranchResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.client.CrudDocumentBranch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CrudDocumentBranchImpl implements CrudDocumentBranch {
    @Override
    public DocumentBranchResponseDto save(DocumentBranchRequestDto documentBranchRequestDto) {
        return null;
    }

    @Override
    public Optional<DocumentBranchResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<DocumentBranchResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<DocumentBranchResponseDto> update(DocumentBranchRequestDto documentBranchRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}

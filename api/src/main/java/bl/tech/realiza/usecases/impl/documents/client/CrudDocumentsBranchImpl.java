package bl.tech.realiza.usecases.impl.documents.client;

import bl.tech.realiza.gateways.requests.documents.client.DocumentsBranchRequestDto;
import bl.tech.realiza.gateways.responses.documents.client.DocumentsBranchResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.client.CrudDocumentsBranch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CrudDocumentsBranchImpl implements CrudDocumentsBranch {
    @Override
    public DocumentsBranchResponseDto save(DocumentsBranchRequestDto documentsBranchRequestDto) {
        return null;
    }

    @Override
    public Optional<DocumentsBranchResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<DocumentsBranchResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<DocumentsBranchResponseDto> update(String id, DocumentsBranchRequestDto documentsBranchRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}

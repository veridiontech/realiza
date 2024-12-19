package bl.tech.realiza.usecases.impl.documents.providers;

import bl.tech.realiza.gateways.repositories.documents.providers.DocumentSubcontractorRepository;
import bl.tech.realiza.gateways.requests.documents.providers.DocumentSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.documents.providers.DocumentSubcontractorResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.providers.CrudDocumentSubcontractor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudDocumentSubcontractorImpl implements CrudDocumentSubcontractor {

    private final DocumentSubcontractorRepository documentSubcontractorRepository;

    @Override
    public DocumentSubcontractorResponseDto save(DocumentSubcontractorRequestDto documentSubcontractorRequestDto) {
        return null;
    }

    @Override
    public Optional<DocumentSubcontractorResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<DocumentSubcontractorResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<DocumentSubcontractorResponseDto> update(DocumentSubcontractorRequestDto documentSubcontractorRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}

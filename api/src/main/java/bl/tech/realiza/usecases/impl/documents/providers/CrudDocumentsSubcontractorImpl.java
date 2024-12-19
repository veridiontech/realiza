package bl.tech.realiza.usecases.impl.documents.providers;

import bl.tech.realiza.gateways.requests.documents.providers.DocumentsSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.documents.providers.DocumentsSubcontractorResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.providers.CrudDocumentsSubcontractor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CrudDocumentsSubcontractorImpl implements CrudDocumentsSubcontractor {
    @Override
    public DocumentsSubcontractorResponseDto save(DocumentsSubcontractorRequestDto documentsSubcontractorRequestDto) {
        return null;
    }

    @Override
    public Optional<DocumentsSubcontractorResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<DocumentsSubcontractorResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<DocumentsSubcontractorResponseDto> update(String id, DocumentsSubcontractorRequestDto documentsSubcontractorRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}

package bl.tech.realiza.usecases.interfaces.documents.providers;

import bl.tech.realiza.gateways.requests.documents.providers.DocumentSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.documents.providers.DocumentSubcontractorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudDocumentSubcontractor {
    DocumentSubcontractorResponseDto save(DocumentSubcontractorRequestDto documentSubcontractorRequestDto);
    Optional<DocumentSubcontractorResponseDto> findOne(String id);
    Page<DocumentSubcontractorResponseDto> findAll(Pageable pageable);
    Optional<DocumentSubcontractorResponseDto> update(DocumentSubcontractorRequestDto documentSubcontractorRequestDto);
    void delete(String id);
}

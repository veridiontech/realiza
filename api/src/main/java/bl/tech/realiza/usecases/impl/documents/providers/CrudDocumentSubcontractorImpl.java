package bl.tech.realiza.usecases.impl.documents.providers;

import bl.tech.realiza.domains.documents.providers.DocumentSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.gateways.repositories.documents.providers.DocumentSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
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
    private final ProviderSubcontractorRepository providerSubcontractorRepository;

    @Override
    public DocumentSubcontractorResponseDto save(DocumentSubcontractorRequestDto documentSubcontractorRequestDto) {

        Optional<ProviderSubcontractor> providerSubcontractorOptional = providerSubcontractorRepository.findById(documentSubcontractorRequestDto.getSubcontractor());

        ProviderSubcontractor providerSubcontractor = providerSubcontractorOptional.orElseThrow(() -> new RuntimeException("Subcontractor not found"));

        DocumentSubcontractor newDocumentSubcontractor = DocumentSubcontractor.builder()
                .title(documentSubcontractorRequestDto.getTitle())
                .risk(documentSubcontractorRequestDto.getRisk())
                .status(documentSubcontractorRequestDto.getStatus())
                .documentation(documentSubcontractorRequestDto.getDocumentation())
                .creation_date(documentSubcontractorRequestDto.getCreation_date())
                .providerSubcontractor(providerSubcontractor)
                .build();

        DocumentSubcontractor savedDocumentSubcontractor = documentSubcontractorRepository.save(newDocumentSubcontractor);

        DocumentSubcontractorResponseDto documentSubcontractorResponse = DocumentSubcontractorResponseDto.builder()
                .id_documentation(savedDocumentSubcontractor.getDocumentation())
                .title(savedDocumentSubcontractor.getTitle())
                .risk(savedDocumentSubcontractor.getRisk())
                .status(savedDocumentSubcontractor.getStatus())
                .documentation(savedDocumentSubcontractor.getDocumentation())
                .creation_date(savedDocumentSubcontractor.getCreation_date())
                .subcontractor(savedDocumentSubcontractor.getProviderSubcontractor().getId_provider())
                .build();

        return documentSubcontractorResponse;
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

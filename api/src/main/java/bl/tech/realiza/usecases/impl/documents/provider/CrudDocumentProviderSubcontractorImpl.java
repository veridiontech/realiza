package bl.tech.realiza.usecases.impl.documents.provider;

import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.requests.documents.provider.DocumentProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.provider.CrudDocumentProviderSubcontractor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudDocumentProviderSubcontractorImpl implements CrudDocumentProviderSubcontractor {

    private final DocumentProviderSubcontractorRepository documentSubcontractorRepository;
    private final ProviderSubcontractorRepository providerSubcontractorRepository;

    @Override
    public DocumentResponseDto save(DocumentProviderSubcontractorRequestDto documentProviderSubcontractorRequestDto) {
        Optional<ProviderSubcontractor> providerSubcontractorOptional = providerSubcontractorRepository.findById(documentProviderSubcontractorRequestDto.getSubcontractor());

        ProviderSubcontractor providerSubcontractor = providerSubcontractorOptional.orElseThrow(() -> new RuntimeException("Subcontractor not found"));

        DocumentProviderSubcontractor newDocumentSubcontractor = DocumentProviderSubcontractor.builder()
                .title(documentProviderSubcontractorRequestDto.getTitle())
                .risk(documentProviderSubcontractorRequestDto.getRisk())
                .status(documentProviderSubcontractorRequestDto.getStatus())
                .documentation(documentProviderSubcontractorRequestDto.getDocumentation())
                .creation_date(documentProviderSubcontractorRequestDto.getCreation_date())
                .providerSubcontractor(providerSubcontractor)
                .build();

        DocumentProviderSubcontractor savedDocumentSubcontractor = documentSubcontractorRepository.save(newDocumentSubcontractor);

        DocumentResponseDto documentSubcontractorResponse = DocumentResponseDto.builder()
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
    public Optional<DocumentResponseDto> findOne(String id) {
        Optional<DocumentProviderSubcontractor> documentSubcontractorOptional = documentSubcontractorRepository.findById(id);

        DocumentProviderSubcontractor documentSubcontractor = documentSubcontractorOptional.orElseThrow(() -> new RuntimeException("Subcontractor not found"));

        DocumentResponseDto documentSubcontractorResponse = DocumentResponseDto.builder()
                .id_documentation(documentSubcontractor.getDocumentation())
                .title(documentSubcontractor.getTitle())
                .risk(documentSubcontractor.getRisk())
                .status(documentSubcontractor.getStatus())
                .documentation(documentSubcontractor.getDocumentation())
                .creation_date(documentSubcontractor.getCreation_date())
                .subcontractor(documentSubcontractor.getProviderSubcontractor().getId_provider())
                .build();

        return Optional.of(documentSubcontractorResponse);
    }

    @Override
    public Page<DocumentResponseDto> findAll(Pageable pageable) {
        Page<DocumentProviderSubcontractor> documentSubcontractorPage = documentSubcontractorRepository.findAll(pageable);

        Page<DocumentResponseDto> documentSubcontractorResponseDtoPage = documentSubcontractorPage.map(
                documentSubcontractor -> DocumentResponseDto.builder()
                        .id_documentation(documentSubcontractor.getDocumentation())
                        .title(documentSubcontractor.getTitle())
                        .risk(documentSubcontractor.getRisk())
                        .status(documentSubcontractor.getStatus())
                        .documentation(documentSubcontractor.getDocumentation())
                        .creation_date(documentSubcontractor.getCreation_date())
                        .subcontractor(documentSubcontractor.getProviderSubcontractor().getId_provider())
                        .build()
        );

        return documentSubcontractorResponseDtoPage;
    }

    @Override
    public Optional<DocumentResponseDto> update(DocumentProviderSubcontractorRequestDto documentProviderSubcontractorRequestDto) {
        Optional<DocumentProviderSubcontractor> documentSubcontractorOptional = documentSubcontractorRepository.findById(documentProviderSubcontractorRequestDto.getId_documentation());

        DocumentProviderSubcontractor documentSubcontractor = documentSubcontractorOptional.orElseThrow(() -> new RuntimeException("Subcontractor not found"));

        documentSubcontractor.setTitle(documentProviderSubcontractorRequestDto.getTitle() != null ? documentProviderSubcontractorRequestDto.getTitle() : documentSubcontractor.getTitle());
        documentSubcontractor.setRisk(documentProviderSubcontractorRequestDto.getRisk() != null ? documentProviderSubcontractorRequestDto.getRisk() : documentSubcontractor.getRisk());
        documentSubcontractor.setStatus(documentProviderSubcontractorRequestDto.getStatus() != null ? documentProviderSubcontractorRequestDto.getStatus() : documentSubcontractor.getStatus());
        documentSubcontractor.setDocumentation(documentProviderSubcontractorRequestDto.getDocumentation() != null ? documentProviderSubcontractorRequestDto.getDocumentation() : documentSubcontractor.getDocumentation());
        documentSubcontractor.setCreation_date(documentProviderSubcontractorRequestDto.getCreation_date() != null ? documentProviderSubcontractorRequestDto.getCreation_date() : documentSubcontractor.getCreation_date());

        DocumentProviderSubcontractor savedDocumentSubcontractor = documentSubcontractorRepository.save(documentSubcontractor);

        DocumentResponseDto documentSubcontractorResponse = DocumentResponseDto.builder()
                .id_documentation(savedDocumentSubcontractor.getDocumentation())
                .title(savedDocumentSubcontractor.getTitle())
                .risk(savedDocumentSubcontractor.getRisk())
                .status(savedDocumentSubcontractor.getStatus())
                .documentation(savedDocumentSubcontractor.getDocumentation())
                .creation_date(savedDocumentSubcontractor.getCreation_date())
                .subcontractor(savedDocumentSubcontractor.getProviderSubcontractor().getId_provider())
                .build();

        return Optional.of(documentSubcontractorResponse);
    }

    @Override
    public void delete(String id) {
        documentSubcontractorRepository.deleteById(id);
    }
}

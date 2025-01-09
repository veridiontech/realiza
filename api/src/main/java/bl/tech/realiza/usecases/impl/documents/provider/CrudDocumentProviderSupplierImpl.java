package bl.tech.realiza.usecases.impl.documents.provider;

import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.documents.provider.DocumentProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.provider.CrudDocumentProviderSupplier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudDocumentProviderSupplierImpl implements CrudDocumentProviderSupplier {

    private final DocumentProviderSupplierRepository documentSupplierRepository;
    private final ProviderSupplierRepository providerSupplierRepository;

    @Override
    public DocumentResponseDto save(DocumentProviderSupplierRequestDto documentProviderSupplierRequestDto) {
        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(documentProviderSupplierRequestDto.getSupplier());

        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new RuntimeException("Provider supplier not found"));

        DocumentProviderSupplier newDocumentSupplier = DocumentProviderSupplier.builder()
                .title(documentProviderSupplierRequestDto.getTitle())
                .risk(documentProviderSupplierRequestDto.getRisk())
                .status(documentProviderSupplierRequestDto.getStatus())
                .documentation(documentProviderSupplierRequestDto.getDocumentation())
                .creation_date(documentProviderSupplierRequestDto.getCreation_date())
                .providerSupplier(providerSupplier)
                .build();

        DocumentProviderSupplier savedDocumentSupplier = documentSupplierRepository.save(newDocumentSupplier);

        DocumentResponseDto documentSupplierResponse = DocumentResponseDto.builder()
                .id_documentation(savedDocumentSupplier.getDocumentation())
                .title(savedDocumentSupplier.getTitle())
                .risk(savedDocumentSupplier.getRisk())
                .status(savedDocumentSupplier.getStatus())
                .documentation(savedDocumentSupplier.getDocumentation())
                .creation_date(savedDocumentSupplier.getCreation_date())
                .supplier(savedDocumentSupplier.getProviderSupplier().getId_provider())
                .build();

        return documentSupplierResponse;
    }

    @Override
    public Optional<DocumentResponseDto> findOne(String id) {
        Optional<DocumentProviderSupplier> documentSupplierOptional = documentSupplierRepository.findById(id);

        DocumentProviderSupplier documentSupplier = documentSupplierOptional.orElseThrow(() -> new RuntimeException("Document supplier not found"));

        DocumentResponseDto documentSupplierResponse = DocumentResponseDto.builder()
                .id_documentation(documentSupplier.getDocumentation())
                .title(documentSupplier.getTitle())
                .risk(documentSupplier.getRisk())
                .status(documentSupplier.getStatus())
                .documentation(documentSupplier.getDocumentation())
                .creation_date(documentSupplier.getCreation_date())
                .supplier(documentSupplier.getProviderSupplier().getId_provider())
                .build();

        return Optional.of(documentSupplierResponse);
    }

    @Override
    public Page<DocumentResponseDto> findAll(Pageable pageable) {
        Page<DocumentProviderSupplier> documentSupplierPage = documentSupplierRepository.findAll(pageable);

        Page<DocumentResponseDto> documentSupplierResponseDtoPage = documentSupplierPage.map(
                documentSupplier -> DocumentResponseDto.builder()
                        .id_documentation(documentSupplier.getDocumentation())
                        .title(documentSupplier.getTitle())
                        .risk(documentSupplier.getRisk())
                        .status(documentSupplier.getStatus())
                        .documentation(documentSupplier.getDocumentation())
                        .creation_date(documentSupplier.getCreation_date())
                        .supplier(documentSupplier.getProviderSupplier().getId_provider())
                        .build()
        );

        return documentSupplierResponseDtoPage;
    }

    @Override
    public Optional<DocumentResponseDto> update(DocumentProviderSupplierRequestDto documentProviderSupplierRequestDto) {
        Optional<DocumentProviderSupplier> documentSupplierOptional = documentSupplierRepository.findById(documentProviderSupplierRequestDto.getId_documentation());

        DocumentProviderSupplier documentSupplier = documentSupplierOptional.orElseThrow(() -> new RuntimeException("Document supplier not found"));

        documentSupplier.setTitle(documentProviderSupplierRequestDto.getTitle() != null ? documentProviderSupplierRequestDto.getTitle() : documentSupplier.getTitle());
        documentSupplier.setRisk(documentProviderSupplierRequestDto.getRisk() != null ? documentProviderSupplierRequestDto.getRisk() : documentSupplier.getRisk());
        documentSupplier.setStatus(documentProviderSupplierRequestDto.getStatus() != null ? documentProviderSupplierRequestDto.getStatus() : documentSupplier.getStatus());
        documentSupplier.setDocumentation(documentProviderSupplierRequestDto.getDocumentation() != null ? documentProviderSupplierRequestDto.getDocumentation() : documentSupplier.getDocumentation());
        documentSupplier.setCreation_date(documentProviderSupplierRequestDto.getCreation_date() != null ? documentProviderSupplierRequestDto.getCreation_date() : documentSupplier.getCreation_date());

        DocumentProviderSupplier savedDocumentSupplier = documentSupplierRepository.save(documentSupplier);

        DocumentResponseDto documentSupplierResponse = DocumentResponseDto.builder()
                .id_documentation(savedDocumentSupplier.getDocumentation())
                .title(savedDocumentSupplier.getTitle())
                .risk(savedDocumentSupplier.getRisk())
                .status(savedDocumentSupplier.getStatus())
                .documentation(savedDocumentSupplier.getDocumentation())
                .creation_date(savedDocumentSupplier.getCreation_date())
                .supplier(savedDocumentSupplier.getProviderSupplier().getId_provider())
                .build();

        return Optional.of(documentSupplierResponse);
    }

    @Override
    public void delete(String id) {
        documentSupplierRepository.deleteById(id);
    }
}

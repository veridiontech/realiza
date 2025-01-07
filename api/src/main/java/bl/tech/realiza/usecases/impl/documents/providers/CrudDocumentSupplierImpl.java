package bl.tech.realiza.usecases.impl.documents.providers;

import bl.tech.realiza.domains.documents.providers.DocumentSupplier;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.gateways.repositories.documents.providers.DocumentSupplierRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.documents.providers.DocumentSupplierRequestDto;
import bl.tech.realiza.gateways.responses.documents.providers.DocumentSupplierResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.providers.CrudDocumentSupplier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudDocumentSupplierImpl implements CrudDocumentSupplier {

    private final DocumentSupplierRepository documentSupplierRepository;
    private final ProviderSupplierRepository providerSupplierRepository;

    @Override
    public DocumentSupplierResponseDto save(DocumentSupplierRequestDto documentSupplierRequestDto) {

        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(documentSupplierRequestDto.getSupplier());

        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new RuntimeException("Provider supplier not found"));

        DocumentSupplier newDocumentSupplier = DocumentSupplier.builder()
                .title(documentSupplierRequestDto.getTitle())
                .risk(documentSupplierRequestDto.getRisk())
                .status(documentSupplierRequestDto.getStatus())
                .documentation(documentSupplierRequestDto.getDocumentation())
                .creation_date(documentSupplierRequestDto.getCreation_date())
                .providerSupplier(providerSupplier)
                .build();

        DocumentSupplier savedDocumentSupplier = documentSupplierRepository.save(newDocumentSupplier);

        DocumentSupplierResponseDto documentSupplierResponse = DocumentSupplierResponseDto.builder()
                .id_documentation(savedDocumentSupplier.getDocumentation())
                .risk(savedDocumentSupplier.getRisk())
                .status(savedDocumentSupplier.getStatus())
                .documentation(savedDocumentSupplier.getDocumentation())
                .creation_date(savedDocumentSupplier.getCreation_date())
                .supplier(savedDocumentSupplier.getProviderSupplier().getId_provider())
                .build();

        return documentSupplierResponse;
    }

    @Override
    public Optional<DocumentSupplierResponseDto> findOne(String id) {

        Optional<DocumentSupplier> documentSupplierOptional = documentSupplierRepository.findById(id);

        DocumentSupplier documentSupplier = documentSupplierOptional.orElseThrow(() -> new RuntimeException("Document supplier not found"));

        DocumentSupplierResponseDto documentSupplierResponse = DocumentSupplierResponseDto.builder()
                .id_documentation(documentSupplier.getDocumentation())
                .risk(documentSupplier.getRisk())
                .status(documentSupplier.getStatus())
                .documentation(documentSupplier.getDocumentation())
                .creation_date(documentSupplier.getCreation_date())
                .supplier(documentSupplier.getProviderSupplier().getId_provider())
                .build();

        return Optional.of(documentSupplierResponse);
    }

    @Override
    public Page<DocumentSupplierResponseDto> findAll(Pageable pageable) {

        Page<DocumentSupplier> documentSupplierPage = documentSupplierRepository.findAll(pageable);

        Page<DocumentSupplierResponseDto> documentSupplierResponseDtoPage = documentSupplierPage.map(
                documentSupplier -> DocumentSupplierResponseDto.builder()
                        .id_documentation(documentSupplier.getDocumentation())
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
    public Optional<DocumentSupplierResponseDto> update(DocumentSupplierRequestDto documentSupplierRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {
        documentSupplierRepository.deleteById(id);
    }
}

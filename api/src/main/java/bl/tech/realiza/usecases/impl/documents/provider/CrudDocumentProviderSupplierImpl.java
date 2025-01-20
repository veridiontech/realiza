package bl.tech.realiza.usecases.impl.documents.provider;

import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.requests.documents.provider.DocumentProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.provider.CrudDocumentProviderSupplier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudDocumentProviderSupplierImpl implements CrudDocumentProviderSupplier {

    private final DocumentProviderSupplierRepository documentSupplierRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final FileRepository fileRepository;

    @Override
    public DocumentResponseDto save(DocumentProviderSupplierRequestDto documentProviderSupplierRequestDto, MultipartFile file) throws IOException {
        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(documentProviderSupplierRequestDto.getSupplier());

        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new RuntimeException("Provider supplier not found"));

        FileDocument fileDocument = FileDocument.builder()
                .name(file.getOriginalFilename())
                .contentType(file.getContentType())
                .data(file.getBytes())
                .build();

        FileDocument savedFileDocument= fileRepository.save(fileDocument);

        DocumentProviderSupplier newDocumentSupplier = DocumentProviderSupplier.builder()
                .title(documentProviderSupplierRequestDto.getTitle())
                .status(documentProviderSupplierRequestDto.getStatus())
                .documentation(savedFileDocument.getIdDocument())
                .providerSupplier(providerSupplier)
                .build();

        DocumentProviderSupplier savedDocumentSupplier = documentSupplierRepository.save(newDocumentSupplier);

        DocumentResponseDto documentSupplierResponse = DocumentResponseDto.builder()
                .idDocumentation(savedDocumentSupplier.getDocumentation())
                .title(savedDocumentSupplier.getTitle())
                .status(savedDocumentSupplier.getStatus())
                .documentation(savedDocumentSupplier.getDocumentation())
                .creationDate(savedDocumentSupplier.getCreationDate())
                .supplier(savedDocumentSupplier.getProviderSupplier().getIdProvider())
                .build();

        return documentSupplierResponse;
    }

    @Override
    public Optional<DocumentResponseDto> findOne(String id) {
        Optional<DocumentProviderSupplier> documentSupplierOptional = documentSupplierRepository.findById(id);

        DocumentProviderSupplier documentSupplier = documentSupplierOptional.orElseThrow(() -> new RuntimeException("Document supplier not found"));

        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(documentSupplier.getDocumentation());
        FileDocument fileDocument = fileDocumentOptional.orElseThrow(() -> new RuntimeException("FileDocument not found"));

        DocumentResponseDto documentSupplierResponse = DocumentResponseDto.builder()
                .idDocumentation(documentSupplier.getDocumentation())
                .title(documentSupplier.getTitle())
                .status(documentSupplier.getStatus())
                .documentation(documentSupplier.getDocumentation())
                .fileName(fileDocument.getIdDocument())
                .fileContentType(fileDocument.getContentType())
                .fileData(fileDocument.getData())
                .creationDate(documentSupplier.getCreationDate())
                .supplier(documentSupplier.getProviderSupplier().getIdProvider())
                .build();

        return Optional.of(documentSupplierResponse);
    }

    @Override
    public Page<DocumentResponseDto> findAll(Pageable pageable) {
        Page<DocumentProviderSupplier> documentSupplierPage = documentSupplierRepository.findAll(pageable);

        Page<DocumentResponseDto> documentSupplierResponseDtoPage = documentSupplierPage.map(
                documentSupplier -> {
                    Optional<FileDocument> fileDocumentOptional = fileRepository.findById(documentSupplier.getDocumentation());
                    FileDocument fileDocument = fileDocumentOptional.orElse(null);

                    return DocumentResponseDto.builder()
                            .idDocumentation(documentSupplier.getDocumentation())
                            .title(documentSupplier.getTitle())
                            .status(documentSupplier.getStatus())
                            .documentation(documentSupplier.getDocumentation())
                            .fileName(fileDocument.getIdDocument())
                            .fileContentType(fileDocument.getContentType())
                            .fileData(fileDocument.getData())
                            .creationDate(documentSupplier.getCreationDate())
                            .supplier(documentSupplier.getProviderSupplier().getIdProvider())
                            .build();
                }
        );

        return documentSupplierResponseDtoPage;
    }

    @Override
    public Optional<DocumentResponseDto> update(DocumentProviderSupplierRequestDto documentProviderSupplierRequestDto, MultipartFile file) throws IOException {
        Optional<DocumentProviderSupplier> documentSupplierOptional = documentSupplierRepository.findById(documentProviderSupplierRequestDto.getIdDocumentation());

        DocumentProviderSupplier documentSupplier = documentSupplierOptional.orElseThrow(() -> new RuntimeException("Document supplier not found"));

        if (file != null && !file.isEmpty()) {
            // Process the file if it exists
            FileDocument fileDocument = FileDocument.builder()
                    .name(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .data(file.getBytes()) // Handle the IOException
                    .build();

            FileDocument savedFileDocument = fileRepository.save(fileDocument);

            // Update the documentBranch with the new file's ID
            documentSupplier.setDocumentation(savedFileDocument.getIdDocument());
        }

        documentSupplier.setTitle(documentProviderSupplierRequestDto.getTitle() != null ? documentProviderSupplierRequestDto.getTitle() : documentSupplier.getTitle());
        documentSupplier.setStatus(documentProviderSupplierRequestDto.getStatus() != null ? documentProviderSupplierRequestDto.getStatus() : documentSupplier.getStatus());
        documentSupplier.setIsActive(documentProviderSupplierRequestDto.getIsActive() != null ? documentProviderSupplierRequestDto.getIsActive() : documentSupplier.getIsActive());

        DocumentProviderSupplier savedDocumentSupplier = documentSupplierRepository.save(documentSupplier);

        DocumentResponseDto documentSupplierResponse = DocumentResponseDto.builder()
                .idDocumentation(savedDocumentSupplier.getDocumentation())
                .title(savedDocumentSupplier.getTitle())
                .status(savedDocumentSupplier.getStatus())
                .documentation(savedDocumentSupplier.getDocumentation())
                .creationDate(savedDocumentSupplier.getCreationDate())
                .supplier(savedDocumentSupplier.getProviderSupplier().getIdProvider())
                .build();

        return Optional.of(documentSupplierResponse);
    }

    @Override
    public void delete(String id) {
        documentSupplierRepository.deleteById(id);
    }
}

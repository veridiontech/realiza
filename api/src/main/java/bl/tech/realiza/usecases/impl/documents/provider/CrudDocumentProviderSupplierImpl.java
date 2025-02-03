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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
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
        FileDocument fileDocument = null;
        String fileDocumentId = null;
        FileDocument savedFileDocument = null;

        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(documentProviderSupplierRequestDto.getSupplier());

        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new EntityNotFoundException("Provider supplier not found"));

        try {
            fileDocument = FileDocument.builder()
                    .name(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .data(file.getBytes())
                    .build();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new EntityNotFoundException(e);
        }

        try {
            savedFileDocument = fileRepository.save(fileDocument);
            fileDocumentId = savedFileDocument.getIdDocumentAsString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new EntityNotFoundException(e);
        }

        DocumentProviderSupplier newDocumentSupplier = DocumentProviderSupplier.builder()
                .title(documentProviderSupplierRequestDto.getTitle())
                .status(documentProviderSupplierRequestDto.getStatus())
                .documentation(fileDocumentId)
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

        DocumentProviderSupplier documentSupplier = documentSupplierOptional.orElseThrow(() -> new EntityNotFoundException("Document supplier not found"));

        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentSupplier.getDocumentation()));
        FileDocument fileDocument = fileDocumentOptional.orElseThrow(() -> new EntityNotFoundException("FileDocument not found"));

        DocumentResponseDto documentSupplierResponse = DocumentResponseDto.builder()
                .idDocumentation(documentSupplier.getDocumentation())
                .title(documentSupplier.getTitle())
                .status(documentSupplier.getStatus())
                .documentation(documentSupplier.getDocumentation())
                .fileName(fileDocument.getName())
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
                    Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentSupplier.getDocumentation()));
                    FileDocument fileDocument = fileDocumentOptional.orElse(null);

                    return DocumentResponseDto.builder()
                            .idDocumentation(documentSupplier.getDocumentation())
                            .title(documentSupplier.getTitle())
                            .status(documentSupplier.getStatus())
                            .documentation(documentSupplier.getDocumentation())
                            .fileName(fileDocument.getName())
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
    public Optional<DocumentResponseDto> update(String id, DocumentProviderSupplierRequestDto documentProviderSupplierRequestDto, MultipartFile file) throws IOException {
        FileDocument fileDocument = null;
        String fileDocumentId = null;
        FileDocument savedFileDocument= null;

        Optional<DocumentProviderSupplier> documentSupplierOptional = documentSupplierRepository.findById(id);

        DocumentProviderSupplier documentSupplier = documentSupplierOptional.orElseThrow(() -> new EntityNotFoundException("Document supplier not found"));

        if (file != null && !file.isEmpty()) {
            try {
                fileDocument = FileDocument.builder()
                        .name(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .data(file.getBytes())
                        .build();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new EntityNotFoundException(e);
            }

            try {
                savedFileDocument = fileRepository.save(fileDocument);
                fileDocumentId = savedFileDocument.getIdDocumentAsString();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw new EntityNotFoundException(e);
            }
            documentSupplier.setDocumentation(fileDocumentId);
        }

        documentSupplier.setTitle(documentProviderSupplierRequestDto.getTitle() != null ? documentProviderSupplierRequestDto.getTitle() : documentSupplier.getTitle());
        documentSupplier.setStatus(documentProviderSupplierRequestDto.getStatus() != null ? documentProviderSupplierRequestDto.getStatus() : documentSupplier.getStatus());

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

    @Override
    public Page<DocumentResponseDto> findAllBySupplier(String idSearch, Pageable pageable) {
        Page<DocumentProviderSupplier> documentSupplierPage = documentSupplierRepository.findAllByProviderSupplier_IdProvider(idSearch, pageable);

        Page<DocumentResponseDto> documentSupplierResponseDtoPage = documentSupplierPage.map(
                documentSupplier -> {
                    Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentSupplier.getDocumentation()));
                    FileDocument fileDocument = fileDocumentOptional.orElse(null);

                    return DocumentResponseDto.builder()
                            .idDocumentation(documentSupplier.getDocumentation())
                            .title(documentSupplier.getTitle())
                            .status(documentSupplier.getStatus())
                            .documentation(documentSupplier.getDocumentation())
                            .fileName(fileDocument.getName())
                            .fileContentType(fileDocument.getContentType())
                            .fileData(fileDocument.getData())
                            .creationDate(documentSupplier.getCreationDate())
                            .supplier(documentSupplier.getProviderSupplier().getIdProvider())
                            .build();
                }
        );

        return documentSupplierResponseDtoPage;
    }
}

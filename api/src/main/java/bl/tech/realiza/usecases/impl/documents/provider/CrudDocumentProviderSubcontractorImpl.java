package bl.tech.realiza.usecases.impl.documents.provider;

import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.requests.documents.provider.DocumentProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.provider.CrudDocumentProviderSubcontractor;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudDocumentProviderSubcontractorImpl implements CrudDocumentProviderSubcontractor {

    private final DocumentProviderSubcontractorRepository documentSubcontractorRepository;
    private final ProviderSubcontractorRepository providerSubcontractorRepository;
    private final FileRepository fileRepository;

    @Override
    public DocumentResponseDto save(DocumentProviderSubcontractorRequestDto documentProviderSubcontractorRequestDto, MultipartFile file) throws IOException {
        Optional<ProviderSubcontractor> providerSubcontractorOptional = providerSubcontractorRepository.findById(documentProviderSubcontractorRequestDto.getSubcontractor());

        ProviderSubcontractor providerSubcontractor = providerSubcontractorOptional.orElseThrow(() -> new EntityNotFoundException("Subcontractor not found"));

        FileDocument fileDocument = FileDocument.builder()
                .name(file.getOriginalFilename())
                .contentType(file.getContentType())
                .data(file.getBytes())
                .build();

        FileDocument savedFileDocument= fileRepository.save(fileDocument);

        DocumentProviderSubcontractor newDocumentSubcontractor = DocumentProviderSubcontractor.builder()
                .title(documentProviderSubcontractorRequestDto.getTitle())
                .status(documentProviderSubcontractorRequestDto.getStatus())
                .documentation(savedFileDocument.getIdDocument())
                .providerSubcontractor(providerSubcontractor)
                .build();

        DocumentProviderSubcontractor savedDocumentSubcontractor = documentSubcontractorRepository.save(newDocumentSubcontractor);

        DocumentResponseDto documentSubcontractorResponse = DocumentResponseDto.builder()
                .idDocumentation(savedDocumentSubcontractor.getDocumentation())
                .title(savedDocumentSubcontractor.getTitle())
                .status(savedDocumentSubcontractor.getStatus())
                .documentation(savedDocumentSubcontractor.getDocumentation())
                .creationDate(savedDocumentSubcontractor.getCreationDate())
                .subcontractor(savedDocumentSubcontractor.getProviderSubcontractor().getIdProvider())
                .build();

        return documentSubcontractorResponse;
    }

    @Override
    public Optional<DocumentResponseDto> findOne(String id) {
        Optional<DocumentProviderSubcontractor> documentSubcontractorOptional = documentSubcontractorRepository.findById(id);

        DocumentProviderSubcontractor documentSubcontractor = documentSubcontractorOptional.orElseThrow(() -> new EntityNotFoundException("Subcontractor not found"));

        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(documentSubcontractor.getDocumentation());
        FileDocument fileDocument = fileDocumentOptional.orElseThrow(() -> new EntityNotFoundException("FileDocument not found"));

        DocumentResponseDto documentSubcontractorResponse = DocumentResponseDto.builder()
                .idDocumentation(documentSubcontractor.getDocumentation())
                .title(documentSubcontractor.getTitle())
                .status(documentSubcontractor.getStatus())
                .documentation(documentSubcontractor.getDocumentation())
                .fileName(fileDocument.getIdDocument())
                .fileContentType(fileDocument.getContentType())
                .fileData(fileDocument.getData())
                .creationDate(documentSubcontractor.getCreationDate())
                .subcontractor(documentSubcontractor.getProviderSubcontractor().getIdProvider())
                .build();

        return Optional.of(documentSubcontractorResponse);
    }

    @Override
    public Page<DocumentResponseDto> findAll(Pageable pageable) {
        Page<DocumentProviderSubcontractor> documentSubcontractorPage = documentSubcontractorRepository.findAll(pageable);

        Page<DocumentResponseDto> documentSubcontractorResponseDtoPage = documentSubcontractorPage.map(
                documentSubcontractor -> {
                    Optional<FileDocument> fileDocumentOptional = fileRepository.findById(documentSubcontractor.getDocumentation());
                    FileDocument fileDocument = fileDocumentOptional.orElse(null);

                    return DocumentResponseDto.builder()
                            .idDocumentation(documentSubcontractor.getDocumentation())
                            .title(documentSubcontractor.getTitle())
                            .status(documentSubcontractor.getStatus())
                            .documentation(documentSubcontractor.getDocumentation())
                            .fileName(fileDocument.getIdDocument())
                            .fileContentType(fileDocument.getContentType())
                            .fileData(fileDocument.getData())
                            .creationDate(documentSubcontractor.getCreationDate())
                            .subcontractor(documentSubcontractor.getProviderSubcontractor().getIdProvider())
                            .build();
                }
        );

        return documentSubcontractorResponseDtoPage;
    }

    @Override
    public Optional<DocumentResponseDto> update(String id, DocumentProviderSubcontractorRequestDto documentProviderSubcontractorRequestDto, MultipartFile file) throws IOException {
        Optional<DocumentProviderSubcontractor> documentSubcontractorOptional = documentSubcontractorRepository.findById(id);

        DocumentProviderSubcontractor documentSubcontractor = documentSubcontractorOptional.orElseThrow(() -> new EntityNotFoundException("Subcontractor not found"));

        if (file != null && !file.isEmpty()) {
            // Process the file if it exists
            FileDocument fileDocument = FileDocument.builder()
                    .name(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .data(file.getBytes()) // Handle the IOException
                    .build();

            FileDocument savedFileDocument = fileRepository.save(fileDocument);

            // Update the documentBranch with the new file's ID
            documentSubcontractor.setDocumentation(savedFileDocument.getIdDocument());
        }

        documentSubcontractor.setTitle(documentProviderSubcontractorRequestDto.getTitle() != null ? documentProviderSubcontractorRequestDto.getTitle() : documentSubcontractor.getTitle());
        documentSubcontractor.setStatus(documentProviderSubcontractorRequestDto.getStatus() != null ? documentProviderSubcontractorRequestDto.getStatus() : documentSubcontractor.getStatus());
        documentSubcontractor.setIsActive(documentProviderSubcontractorRequestDto.getIsActive() != null ? documentProviderSubcontractorRequestDto.getIsActive() : documentSubcontractor.getIsActive());

        DocumentProviderSubcontractor savedDocumentSubcontractor = documentSubcontractorRepository.save(documentSubcontractor);

        DocumentResponseDto documentSubcontractorResponse = DocumentResponseDto.builder()
                .idDocumentation(savedDocumentSubcontractor.getDocumentation())
                .title(savedDocumentSubcontractor.getTitle())
                .status(savedDocumentSubcontractor.getStatus())
                .documentation(savedDocumentSubcontractor.getDocumentation())
                .creationDate(savedDocumentSubcontractor.getCreationDate())
                .subcontractor(savedDocumentSubcontractor.getProviderSubcontractor().getIdProvider())
                .build();

        return Optional.of(documentSubcontractorResponse);
    }

    @Override
    public void delete(String id) {
        documentSubcontractorRepository.deleteById(id);
    }

    @Override
    public Page<DocumentResponseDto> findAllBySubcontractor(String idSearch, Pageable pageable) {
        Page<DocumentProviderSubcontractor> documentSubcontractorPage = documentSubcontractorRepository.findAllByProviderSubcontractor_IdProvider(idSearch, pageable);

        Page<DocumentResponseDto> documentSubcontractorResponseDtoPage = documentSubcontractorPage.map(
                documentSubcontractor -> {
                    Optional<FileDocument> fileDocumentOptional = fileRepository.findById(documentSubcontractor.getDocumentation());
                    FileDocument fileDocument = fileDocumentOptional.orElse(null);

                    return DocumentResponseDto.builder()
                            .idDocumentation(documentSubcontractor.getDocumentation())
                            .title(documentSubcontractor.getTitle())
                            .status(documentSubcontractor.getStatus())
                            .documentation(documentSubcontractor.getDocumentation())
                            .fileName(fileDocument.getIdDocument())
                            .fileContentType(fileDocument.getContentType())
                            .fileData(fileDocument.getData())
                            .creationDate(documentSubcontractor.getCreationDate())
                            .subcontractor(documentSubcontractor.getProviderSubcontractor().getIdProvider())
                            .build();
                }
        );

        return documentSubcontractorResponseDtoPage;
    }
}

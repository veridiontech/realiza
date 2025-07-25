package bl.tech.realiza.usecases.impl.documents.client;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.documents.client.DocumentClient;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentClientRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.requests.documents.client.DocumentClientRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.services.GoogleCloudService;
import bl.tech.realiza.services.documentProcessing.DocumentProcessingService;
import bl.tech.realiza.usecases.interfaces.documents.client.CrudDocumentClient;
import bl.tech.realiza.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;

import static bl.tech.realiza.domains.documents.Document.Status.*;

@Service
@RequiredArgsConstructor
public class CrudDocumentClientImpl implements CrudDocumentClient {

    private final DocumentClientRepository documentClientRepository;
    private final ClientRepository clientRepository;
    private final FileRepository fileRepository;
    private final DocumentProcessingService documentProcessingService;
    private final GoogleCloudService googleCloudService;

    @Override
    public DocumentResponseDto save(DocumentClientRequestDto documentClientRequestDto) {
        if (documentClientRequestDto.getClient() == null || documentClientRequestDto.getClient().isEmpty()) {
            throw new BadRequestException("Invalid client");
        }

        Client client = clientRepository.findById(documentClientRequestDto.getClient())
                .orElseThrow(() -> new NotFoundException("Client not found"));

        DocumentClient savedDocumentClient = documentClientRepository.save(DocumentClient.builder()
                .title(documentClientRequestDto.getTitle())
                .status(documentClientRequestDto.getStatus())
                .client(client)
                .build());

        return DocumentResponseDto.builder()
                .idDocument(savedDocumentClient.getIdDocumentation())
                .title(savedDocumentClient.getTitle())
                .status(savedDocumentClient.getStatus())
                .creationDate(savedDocumentClient.getCreationDate())
                .client(savedDocumentClient.getClient() != null
                        ? savedDocumentClient.getClient().getIdClient()
                        : null)
                .build();
    }

    @Override
    public Optional<DocumentResponseDto> findOne(String id) {

        DocumentClient documentClient = documentClientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found"));

        String signedUrl = null;

        FileDocument fileDocument = documentClient.getDocument().stream()
                .max(Comparator.comparing(FileDocument::getCreationDate))
                .orElse(null);
        if (fileDocument != null) {
            if (fileDocument.getUrl() != null) {
                signedUrl = googleCloudService.generateSignedUrl(fileDocument.getUrl(), 15);
            }
        }

        return Optional.of(DocumentResponseDto.builder()
                .idDocument(documentClient.getIdDocumentation())
                .title(documentClient.getTitle())
                .status(documentClient.getStatus())
                .signedUrl(signedUrl)
                .creationDate(documentClient.getCreationDate())
                .client(documentClient.getClient().getIdClient())
                .build());
    }

    @Override
    public Page<DocumentResponseDto> findAll(Pageable pageable) {
        Page<DocumentClient> documentClientPage = documentClientRepository.findAll(pageable);

        return documentClientPage.map(
                documentClient -> {
                    String signedUrl = null;
                    FileDocument fileDocument = documentClient.getDocument().stream()
                            .max(Comparator.comparing(FileDocument::getCreationDate))
                            .orElse(null);
                    if (fileDocument != null) {
                        if (fileDocument.getUrl() != null) {
                            signedUrl = googleCloudService.generateSignedUrl(fileDocument.getUrl(), 15);
                        }
                    }

                    return DocumentResponseDto.builder()
                            .idDocument(documentClient.getIdDocumentation())
                            .title(documentClient.getTitle())
                            .status(documentClient.getStatus())
                            .signedUrl(signedUrl)
                            .creationDate(documentClient.getCreationDate())
                            .client(documentClient.getClient() != null
                                    ? documentClient.getClient().getIdClient()
                                    : null)
                            .build();
                }
        );
    }

    @Override
    public Optional<DocumentResponseDto> update(String id, DocumentClientRequestDto documentClientRequestDto) {
        DocumentClient documentClient = documentClientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found"));

        documentClient.setStatus(documentClientRequestDto.getStatus() != null
                ? documentClientRequestDto.getStatus()
                : documentClient.getStatus());

        DocumentClient updatedDocumentClient = documentClientRepository.save(documentClient);

        DocumentResponseDto documentClientResponse = DocumentResponseDto.builder()
                .idDocument(updatedDocumentClient.getIdDocumentation())
                .title(updatedDocumentClient.getTitle())
                .status(updatedDocumentClient.getStatus())
                .creationDate(updatedDocumentClient.getCreationDate())
                .client(updatedDocumentClient.getClient() != null
                        ? updatedDocumentClient.getClient().getIdClient()
                        : null)
                .build();

        return Optional.of(documentClientResponse);
    }

    @Override
    public void delete(String id) {
        documentClientRepository.deleteById(id);
    }

    @Override
    public Optional<DocumentResponseDto> upload(String id, MultipartFile file) throws IOException {
        if (file != null) {
            if (file.getSize() > 5 * 1024 * 1024) { // 5 MB
                throw new BadRequestException("Arquivo muito grande.");
            }
        }

        FileDocument savedFileDocument= null;
        String signedUrl = null;

        DocumentClient documentClient = documentClientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document client not found"));

        if (file != null && !file.isEmpty()) {
            try {
                String gcsUrl = googleCloudService.uploadFile(file, "client-documents");

                savedFileDocument = fileRepository.save(FileDocument.builder()
                        .name(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .url(gcsUrl)
                        .document(documentClient)
                        .build());
                signedUrl = googleCloudService.generateSignedUrl(savedFileDocument.getUrl(), 15);

            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new IOException(e);
            }
            documentClient.setStatus(EM_ANALISE);
            documentClient.setAdherent(true);
            documentClient.setConforming(false);
        }

        DocumentClient savedDocumentClient = documentClientRepository.save(documentClient);

        documentProcessingService.processDocumentAsync(file,
                (DocumentClient) Hibernate.unproxy(documentClient));

        return Optional.of(DocumentResponseDto.builder()
                .idDocument(savedDocumentClient.getIdDocumentation())
                .title(savedDocumentClient.getTitle())
                .status(savedDocumentClient.getStatus())
                .creationDate(savedDocumentClient.getCreationDate())
                .signedUrl(signedUrl)
                .client(savedDocumentClient.getClient() != null
                        ? savedDocumentClient.getClient().getIdClient()
                        : null)
                .build());
    }

    @Override
    public Page<DocumentResponseDto> findAllByClient(String idSearch, Pageable pageable) {
        Page<DocumentClient> documentClientPage = documentClientRepository.findAllByClient_IdClient(idSearch, pageable);

        return documentClientPage.map(
                documentClient -> {
                    String signedUrl = null;
                    FileDocument fileDocument = documentClient.getDocument().stream()
                            .max(Comparator.comparing(FileDocument::getCreationDate))
                            .orElse(null);
                    if (fileDocument != null) {
                        if (fileDocument.getUrl() != null) {
                            signedUrl = googleCloudService.generateSignedUrl(fileDocument.getUrl(), 15);
                        }
                    }

                    return DocumentResponseDto.builder()
                            .idDocument(documentClient.getIdDocumentation())
                            .title(documentClient.getTitle())
                            .status(documentClient.getStatus())
                            .signedUrl(signedUrl)
                            .creationDate(documentClient.getCreationDate())
                            .client(documentClient.getClient() != null
                                    ? documentClient.getClient().getIdClient()
                                    : null)
                            .build();
                }
        );
    }
}

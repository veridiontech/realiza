package bl.tech.realiza.usecases.impl.documents.client;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.client.DocumentClient;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentClientRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.requests.documents.client.DocumentClientRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.gateways.responses.services.DocumentIAValidationResponse;
import bl.tech.realiza.services.documentProcessing.DocumentProcessingService;
import bl.tech.realiza.usecases.interfaces.documents.client.CrudDocumentClient;
import bl.tech.realiza.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudDocumentClientImpl implements CrudDocumentClient {

    private final DocumentClientRepository documentClientRepository;
    private final ClientRepository clientRepository;
    private final FileRepository fileRepository;
    private final DocumentProcessingService documentProcessingService;

    @Override
    public DocumentResponseDto save(DocumentClientRequestDto documentClientRequestDto, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Invalid file");
        }
        if (documentClientRequestDto.getClient() == null || documentClientRequestDto.getClient().isEmpty()) {
            throw new BadRequestException("Invalid client");
        }

        FileDocument fileDocument = null;
        String fileDocumentId = null;
        FileDocument savedFileDocument= null;

        Optional<Client> clientOptional = clientRepository.findById(documentClientRequestDto.getClient());

        Client client = clientOptional.orElseThrow(() -> new NotFoundException("Client not found"));

        try {
            fileDocument = FileDocument.builder()
                    .name(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .data(file.getBytes())
                    .build();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new IOException("Document couldn't be built");
        }

        try {
            savedFileDocument = fileRepository.save(fileDocument);
            fileDocumentId = savedFileDocument.getIdDocumentAsString(); // Garante que seja uma String válida
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new NotFoundException("Document couldn't be saved");
        }

        DocumentClient newDocumentClient = DocumentClient.builder()
                .title(documentClientRequestDto.getTitle())
                .status(documentClientRequestDto.getStatus())
                .documentation(fileDocumentId)
                .client(client)
                .build();

        DocumentClient savedDocumentClient = documentClientRepository.save(newDocumentClient);

        DocumentResponseDto documentClientResponse = DocumentResponseDto.builder()
                .idDocument(savedDocumentClient.getIdDocumentation())
                .title(savedDocumentClient.getTitle())
                .status(savedDocumentClient.getStatus())
                .documentation(savedDocumentClient.getDocumentation())
                .creationDate(savedDocumentClient.getCreationDate())
                .client(savedDocumentClient.getClient().getIdClient())
                .build();

        return documentClientResponse;
    }

    @Override
    public Optional<DocumentResponseDto> findOne(String id) {
        Optional<DocumentClient> documentClientOptional = documentClientRepository.findById(id);

        DocumentClient documentClient = documentClientOptional.orElseThrow(() -> new NotFoundException("Document not found"));

        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentClient.getDocumentation()));

        FileDocument fileDocument = fileDocumentOptional.orElseThrow(() -> new NotFoundException("FileDocument not found"));

        DocumentResponseDto documentClientResponseDto = DocumentResponseDto.builder()
                .idDocument(documentClient.getIdDocumentation())
                .title(documentClient.getTitle())
                .status(documentClient.getStatus())
                .documentation(documentClient.getDocumentation())
                .fileName(fileDocument.getName())
                .fileContentType(fileDocument.getContentType())
                .fileData(fileDocument.getData())
                .creationDate(documentClient.getCreationDate())
                .client(documentClient.getClient().getIdClient())
                .build();

        return Optional.of(documentClientResponseDto);
    }

    @Override
    public Page<DocumentResponseDto> findAll(Pageable pageable) {
        Page<DocumentClient> documentClientPage = documentClientRepository.findAll(pageable);

        Page<DocumentResponseDto> documentClientResponseDtoPage = documentClientPage.map(
                documentClient -> {
                    FileDocument fileDocument = null;
                    if (documentClient.getDocumentation() != null && ObjectId.isValid(documentClient.getDocumentation())) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentClient.getDocumentation()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }

                    return DocumentResponseDto.builder()
                            .idDocument(documentClient.getIdDocumentation())
                            .title(documentClient.getTitle())
                            .status(documentClient.getStatus())
                            .documentation(documentClient.getDocumentation())
                            .fileName(fileDocument != null ? fileDocument.getName() : null)
                            .fileContentType(fileDocument != null ? fileDocument.getContentType() : null)
                            .fileData(fileDocument != null ? fileDocument.getData() : null)
                            .creationDate(documentClient.getCreationDate())
                            .client(documentClient.getClient().getIdClient())
                            .build();
                }
        );

        return documentClientResponseDtoPage;
    }

    @Override
    public Optional<DocumentResponseDto> update(String id, DocumentClientRequestDto documentClientRequestDto, MultipartFile file) throws IOException {
        FileDocument fileDocument = null;
        String fileDocumentId = null;
        FileDocument savedFileDocument= null;

        Optional<DocumentClient> documentClientOptional = documentClientRepository.findById(id);

        DocumentClient documentClient = documentClientOptional.orElseThrow(() -> new NotFoundException("Document not found"));

        if (file != null && !file.isEmpty()) {
            try {
                fileDocument = FileDocument.builder()
                        .name(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .data(file.getBytes())
                        .build();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new IOException("Document couldn't be built");
            }

            try {
                savedFileDocument = fileRepository.save(fileDocument);
                fileDocumentId = savedFileDocument.getIdDocumentAsString();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw new NotFoundException("Document couldn't be saved");
            }
            documentClient.setDocumentation(fileDocumentId);
        }

        documentClient.setStatus(documentClientRequestDto.getStatus() != null ? documentClientRequestDto.getStatus() : documentClient.getStatus());

        DocumentClient savedDocumentClient = documentClientRepository.save(documentClient);

        DocumentResponseDto documentClientResponse = DocumentResponseDto.builder()
                .idDocument(savedDocumentClient.getIdDocumentation())
                .title(savedDocumentClient.getTitle())
                .status(savedDocumentClient.getStatus())
                .documentation(savedDocumentClient.getDocumentation())
                .creationDate(savedDocumentClient.getCreationDate())
                .client(savedDocumentClient.getClient().getIdClient())
                .build();

        return Optional.of(documentClientResponse);
    }

    @Override
    public void delete(String id) {
        documentClientRepository.deleteById(id);
    }

    @Override
    public Optional<DocumentResponseDto> upload(String id, MultipartFile file) throws IOException {
        FileDocument fileDocument = null;
        String fileDocumentId = null;
        FileDocument savedFileDocument= null;

        DocumentClient documentClient = documentClientRepository.findById(id).orElseThrow(() -> new NotFoundException("Document client not found"));

        if (file != null && !file.isEmpty()) {
            try {
                fileDocument = FileDocument.builder()
                        .name(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .data(file.getBytes())
                        .build();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new IOException("Document couldn't be built");
            }

            try {
                savedFileDocument = fileRepository.save(fileDocument);
                fileDocumentId = savedFileDocument.getIdDocumentAsString();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw new NotFoundException("Document couldn't be saved");
            }
            documentClient.setDocumentation(fileDocumentId);
            documentClient.setStatus(Document.Status.EM_ANALISE);
        }

        documentProcessingService.processDocumentAsync(file,
                (DocumentClient) Hibernate.unproxy(documentClient));

        DocumentClient savedDocumentClient = documentClientRepository.save(documentClient);

        DocumentResponseDto documentClientResponse = DocumentResponseDto.builder()
                .idDocument(savedDocumentClient.getIdDocumentation())
                .title(savedDocumentClient.getTitle())
                .status(savedDocumentClient.getStatus())
                .documentation(savedDocumentClient.getDocumentation())
                .creationDate(savedDocumentClient.getCreationDate())
                .client(savedDocumentClient.getClient().getIdClient())
                .build();

        return Optional.of(documentClientResponse);
    }

    @Override
    public Page<DocumentResponseDto> findAllByClient(String idSearch, Pageable pageable) {
        Page<DocumentClient> documentClientPage = documentClientRepository.findAllByClient_IdClient(idSearch, pageable);

        Page<DocumentResponseDto> documentClientResponseDtoPage = documentClientPage.map(
                documentClient -> {
                    FileDocument fileDocument = null;
                    if (documentClient.getDocumentation() != null && ObjectId.isValid(documentClient.getDocumentation())) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentClient.getDocumentation()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }

                    return DocumentResponseDto.builder()
                            .idDocument(documentClient.getIdDocumentation())
                            .title(documentClient.getTitle())
                            .status(documentClient.getStatus())
                            .documentation(documentClient.getDocumentation())
                            .fileName(fileDocument != null ? fileDocument.getName() : null)
                            .fileContentType(fileDocument != null ? fileDocument.getContentType() : null)
                            .fileData(fileDocument != null ? fileDocument.getData() : null)
                            .creationDate(documentClient.getCreationDate())
                            .client(documentClient.getClient().getIdClient())
                            .build();
                }
        );

        return documentClientResponseDtoPage;
    }
}

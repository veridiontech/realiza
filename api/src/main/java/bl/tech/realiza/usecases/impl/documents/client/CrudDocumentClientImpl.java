package bl.tech.realiza.usecases.impl.documents.client;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.documents.client.DocumentClient;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentClientRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.requests.documents.client.DocumentClientRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.client.CrudDocumentClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudDocumentClientImpl implements CrudDocumentClient {

    private final DocumentClientRepository documentClientRepository;
    private final ClientRepository clientRepository;
    private final FileRepository fileRepository;

    @Override
    public DocumentResponseDto save(DocumentClientRequestDto documentClientRequestDto, MultipartFile file) throws IOException {
        Optional<Client> clientOptional = clientRepository.findById(documentClientRequestDto.getClient());

        Client client = clientOptional.orElseThrow(() -> new RuntimeException("Client not found"));

        FileDocument fileDocument = null;
        try {
            fileDocument = FileDocument.builder()
                    .name(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .data(file.getBytes())
                    .build();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        FileDocument savedFileDocument= null;
        try {
            savedFileDocument = fileRepository.save(fileDocument);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        DocumentClient newDocumentClient = DocumentClient.builder()
                .title(documentClientRequestDto.getTitle())
                .status(documentClientRequestDto.getStatus())
                .documentation(savedFileDocument.getIdDocument())
                .client(client)
                .build();

        DocumentClient savedDocumentClient = documentClientRepository.save(newDocumentClient);

        DocumentResponseDto documentClientResponse = DocumentResponseDto.builder()
                .idDocumentation(savedDocumentClient.getIdDocumentation())
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

        DocumentClient documentClient = documentClientOptional.orElseThrow(() -> new RuntimeException("Document not found"));

        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(documentClient.getDocumentation());
        FileDocument fileDocument = fileDocumentOptional.orElseThrow(() -> new RuntimeException("FileDocument not found"));

        DocumentResponseDto documentClientResponseDto = DocumentResponseDto.builder()
                .idDocumentation(documentClient.getIdDocumentation())
                .title(documentClient.getTitle())
                .status(documentClient.getStatus())
                .documentation(fileDocument.getIdDocument())
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
                    Optional<FileDocument> fileDocumentOptional = fileRepository.findById(documentClient.getDocumentation());
                    FileDocument fileDocument = fileDocumentOptional.orElse(null);

                    return DocumentResponseDto.builder()
                            .idDocumentation(documentClient.getIdDocumentation())
                            .title(documentClient.getTitle())
                            .status(documentClient.getStatus())
                            .documentation(documentClient.getDocumentation())
                            .fileName(fileDocument.getName())
                            .fileContentType(fileDocument.getContentType())
                            .fileData(fileDocument.getData())
                            .creationDate(documentClient.getCreationDate())
                            .client(documentClient.getClient().getIdClient())
                            .build();
                }
        );

        return documentClientResponseDtoPage;
    }

    @Override
    public Optional<DocumentResponseDto> update(DocumentClientRequestDto documentClientRequestDto, MultipartFile file) throws IOException {
        Optional<DocumentClient> documentClientOptional = documentClientRepository.findById(documentClientRequestDto.getIdDocumentation());

        DocumentClient documentClient = documentClientOptional.orElseThrow(() -> new RuntimeException("Document not found"));

        if (file != null && !file.isEmpty()) {
            // Process the file if it exists
            FileDocument fileDocument = FileDocument.builder()
                    .name(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .data(file.getBytes()) // Handle the IOException
                    .build();

            FileDocument savedFileDocument = fileRepository.save(fileDocument);

            // Update the documentBranch with the new file's ID
            documentClient.setDocumentation(savedFileDocument.getIdDocument());
        }

        documentClient.setTitle(documentClientRequestDto.getTitle() != null ? documentClientRequestDto.getTitle() : documentClient.getTitle());
        documentClient.setStatus(documentClientRequestDto.getStatus() != null ? documentClientRequestDto.getStatus() : documentClient.getStatus());
        documentClient.setIsActive(documentClientRequestDto.getIsActive() != null ? documentClientRequestDto.getIsActive() : documentClient.getIsActive());

        DocumentClient savedDocumentClient = documentClientRepository.save(documentClient);

        DocumentResponseDto documentClientResponse = DocumentResponseDto.builder()
                .idDocumentation(savedDocumentClient.getIdDocumentation())
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
}

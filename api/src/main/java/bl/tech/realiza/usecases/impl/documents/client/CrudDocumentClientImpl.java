package bl.tech.realiza.usecases.impl.documents.client;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.documents.client.DocumentClient;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentClientRepository;
import bl.tech.realiza.gateways.requests.documents.client.DocumentClientRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.client.CrudDocumentClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudDocumentClientImpl implements CrudDocumentClient {

    private final DocumentClientRepository documentClientRepository;
    private final ClientRepository clientRepository;

    @Override
    public DocumentResponseDto save(DocumentClientRequestDto documentClientRequestDto) {
        Optional<Client> clientOptional = clientRepository.findById(documentClientRequestDto.getClient());

        Client client = clientOptional.orElseThrow(() -> new RuntimeException("Client not found"));

        DocumentClient newDocumentClient = DocumentClient.builder()
                .title(documentClientRequestDto.getTitle())
                .risk(documentClientRequestDto.getRisk())
                .status(documentClientRequestDto.getStatus())
                .documentation(documentClientRequestDto.getDocumentation())
                .creationDate(documentClientRequestDto.getCreationDate())
                .client(client)
                .build();

        DocumentClient savedDocumentClient = documentClientRepository.save(newDocumentClient);

        DocumentResponseDto documentClientResponse = DocumentResponseDto.builder()
                .idDocumentation(savedDocumentClient.getIdDocumentation())
                .title(savedDocumentClient.getTitle())
                .risk(savedDocumentClient.getRisk())
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

        DocumentResponseDto documentClientResponseDto = DocumentResponseDto.builder()
                .idDocumentation(documentClient.getIdDocumentation())
                .title(documentClient.getTitle())
                .risk(documentClient.getRisk())
                .status(documentClient.getStatus())
                .documentation(documentClient.getDocumentation())
                .creationDate(documentClient.getCreationDate())
                .client(documentClient.getClient().getIdClient())
                .build();

        return Optional.of(documentClientResponseDto);
    }

    @Override
    public Page<DocumentResponseDto> findAll(Pageable pageable) {
        Page<DocumentClient> documentClientPage = documentClientRepository.findAll(pageable);

        Page<DocumentResponseDto> documentClientResponseDtoPage = documentClientPage.map(
                documentClient -> DocumentResponseDto.builder()
                        .idDocumentation(documentClient.getIdDocumentation())
                        .title(documentClient.getTitle())
                        .risk(documentClient.getRisk())
                        .status(documentClient.getStatus())
                        .documentation(documentClient.getDocumentation())
                        .creationDate(documentClient.getCreationDate())
                        .client(documentClient.getClient().getIdClient())
                        .build()
        );

        return documentClientResponseDtoPage;
    }

    @Override
    public Optional<DocumentResponseDto> update(DocumentClientRequestDto documentClientRequestDto) {
        Optional<DocumentClient> documentClientOptional = documentClientRepository.findById(documentClientRequestDto.getIdDocumentation());

        DocumentClient documentClient = documentClientOptional.orElseThrow(() -> new RuntimeException("Document not found"));

        documentClient.setTitle(documentClientRequestDto.getTitle() != null ? documentClientRequestDto.getTitle() : documentClient.getTitle());
        documentClient.setRisk(documentClientRequestDto.getRisk() != null ? documentClientRequestDto.getRisk() : documentClient.getRisk());
        documentClient.setStatus(documentClientRequestDto.getStatus() != null ? documentClientRequestDto.getStatus() : documentClient.getStatus());
        documentClient.setDocumentation(documentClientRequestDto.getDocumentation() != null ? documentClientRequestDto.getDocumentation() : documentClient.getDocumentation());
        documentClient.setCreationDate(documentClientRequestDto.getCreationDate() != null ? documentClientRequestDto.getCreationDate() : documentClient.getCreationDate());

        DocumentClient savedDocumentClient = documentClientRepository.save(documentClient);

        DocumentResponseDto documentClientResponse = DocumentResponseDto.builder()
                .idDocumentation(savedDocumentClient.getIdDocumentation())
                .title(savedDocumentClient.getTitle())
                .risk(savedDocumentClient.getRisk())
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

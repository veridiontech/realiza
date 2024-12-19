package bl.tech.realiza.usecases.impl.documents.client;

import bl.tech.realiza.gateways.repositories.documents.client.DocumentClientRepository;
import bl.tech.realiza.gateways.requests.documents.client.DocumentClientRequestDto;
import bl.tech.realiza.gateways.responses.documents.client.DocumentClientResponseDto;
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

    @Override
    public DocumentClientResponseDto save(DocumentClientRequestDto documentClientRequestDto) {
        return null;
    }

    @Override
    public Optional<DocumentClientResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<DocumentClientResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<DocumentClientResponseDto> update(DocumentClientRequestDto documentClientRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}

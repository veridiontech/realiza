package bl.tech.realiza.usecases.impl.documents.client;

import bl.tech.realiza.gateways.requests.documents.client.DocumentsClientRequestDto;
import bl.tech.realiza.gateways.responses.documents.client.DocumentsClientResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.client.CrudDocumentsClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CrudDocumentsClientImpl implements CrudDocumentsClient {
    @Override
    public DocumentsClientResponseDto save(DocumentsClientRequestDto documentsClientRequestDto) {
        return null;
    }

    @Override
    public Optional<DocumentsClientResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<DocumentsClientResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<DocumentsClientResponseDto> update(DocumentsClientRequestDto documentsClientRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}

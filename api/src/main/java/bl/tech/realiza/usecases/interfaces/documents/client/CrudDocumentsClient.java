package bl.tech.realiza.usecases.interfaces.documents.client;

import bl.tech.realiza.gateways.requests.documents.client.DocumentsClientRequestDto;
import bl.tech.realiza.gateways.responses.documents.client.DocumentsClientResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudDocumentsClient {
    DocumentsClientResponseDto save(DocumentsClientRequestDto documentsClientRequestDto);
    Optional<DocumentsClientResponseDto> findOne(String id);
    Page<DocumentsClientResponseDto> findAll(Pageable pageable);
    Optional<DocumentsClientResponseDto> update(DocumentsClientRequestDto documentsClientRequestDto);
    void delete(String id);
}

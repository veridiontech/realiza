package bl.tech.realiza.usecases.interfaces.documents.client;

import bl.tech.realiza.gateways.requests.documents.client.DocumentClientRequestDto;
import bl.tech.realiza.gateways.responses.documents.client.DocumentClientResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudDocumentClient {
    DocumentClientResponseDto save(DocumentClientRequestDto documentClientRequestDto);
    Optional<DocumentClientResponseDto> findOne(String id);
    Page<DocumentClientResponseDto> findAll(Pageable pageable);
    Optional<DocumentClientResponseDto> update(DocumentClientRequestDto documentClientRequestDto);
    void delete(String id);
}

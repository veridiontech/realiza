package bl.tech.realiza.gateways.controllers.impl.documents.client;

import bl.tech.realiza.gateways.controllers.interfaces.documents.client.DocumentClientController;
import bl.tech.realiza.gateways.requests.documents.client.DocumentClientRequestDto;
import bl.tech.realiza.gateways.responses.documents.client.DocumentClientResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class DocumentClientControllerImpl implements DocumentClientController {
    @Override
    public ResponseEntity<DocumentClientResponseDto> createDocumentClient(DocumentClientRequestDto documentClientRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<DocumentClientResponseDto>> getOneDocumentClient(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Page<DocumentClientResponseDto>> getAllDocumentsClient(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<DocumentClientResponseDto>> updateDocumentClient(DocumentClientRequestDto documentClientRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteDocumentClient(String id) {
        return null;
    }
}

package bl.tech.realiza.gateways.controllers.impl.clients;

import bl.tech.realiza.gateways.controllers.interfaces.clients.ClientController;
import bl.tech.realiza.gateways.requests.clients.ClientRequestDto;
import bl.tech.realiza.gateways.responses.clients.ClientResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class ClientControllerImpl implements ClientController {
    @Override
    public ResponseEntity<ClientResponseDto> createClient(ClientRequestDto clientRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<ClientResponseDto>> getOneClient(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Page<ClientResponseDto>> getAllClients(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<ClientResponseDto>> updateClient(ClientRequestDto clientRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteClient(String id) {
        return null;
    }
}

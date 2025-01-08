package bl.tech.realiza.gateways.controllers.interfaces.clients;

import bl.tech.realiza.gateways.requests.clients.ClientRequestDto;
import bl.tech.realiza.gateways.responses.clients.ClientResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface ClientControlller {
    ResponseEntity<ClientResponseDto> createClient(ClientRequestDto clientRequestDto);
    ResponseEntity<Optional<ClientResponseDto>> getOneClient(String id);
    ResponseEntity<Page<ClientResponseDto>> getAllClients(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<ClientResponseDto>> updateClient(ClientRequestDto clientRequestDto);
    ResponseEntity<Void> deleteClient(String id);
}

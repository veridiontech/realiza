package bl.tech.realiza.gateways.controllers.interfaces.clients;

import bl.tech.realiza.gateways.requests.clients.client.ClientRequestDto;
import bl.tech.realiza.gateways.responses.clients.ClientResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ClientControlller {
    ResponseEntity<ClientResponseDto> createClient(ClientRequestDto clientRequestDto);
    ResponseEntity<Optional<ClientResponseDto>> getOneClient(String id);
    ResponseEntity<Page<ClientResponseDto>> getAllClients(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<ClientResponseDto>> updateClient(String id, ClientRequestDto clientRequestDto);
    ResponseEntity<String> updateLogo(String id, MultipartFile file);
    ResponseEntity<Void> deleteClient(String id);
    ResponseEntity<Optional<ClientResponseDto>> getClientByBranch(String idBranch);
}

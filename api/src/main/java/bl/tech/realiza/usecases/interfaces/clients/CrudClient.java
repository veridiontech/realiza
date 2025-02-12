package bl.tech.realiza.usecases.interfaces.clients;

import bl.tech.realiza.gateways.requests.clients.client.ClientRequestDto;
import bl.tech.realiza.gateways.responses.clients.ClientResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface CrudClient {
    ClientResponseDto save(ClientRequestDto clientRequestDto);
    Optional<ClientResponseDto> findOne(String id);
    Page<ClientResponseDto> findAll(Pageable pageable);
    Optional<ClientResponseDto> update(String id, ClientRequestDto clientRequestDto);
    void delete(String id);
    String changeLogo(String id, MultipartFile file) throws IOException;
}

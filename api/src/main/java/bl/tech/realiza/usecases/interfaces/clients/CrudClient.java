package bl.tech.realiza.usecases.interfaces.clients;

import bl.tech.realiza.gateways.requests.clients.ClientAndUserClientRequestDto;
import bl.tech.realiza.gateways.requests.clients.ClientRequestDto;
import bl.tech.realiza.gateways.responses.clients.ClientAndUserClientResponseDto;
import bl.tech.realiza.gateways.responses.clients.ClientResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudClient {
    ClientResponseDto save(ClientRequestDto clientRequestDto);
    Optional<ClientResponseDto> findOne(String id);
    Page<ClientResponseDto> findAll(Pageable pageable);
    Optional<ClientResponseDto> update(ClientRequestDto clientRequestDto);
    void delete(String id);
    ClientAndUserClientResponseDto saveBoth(ClientAndUserClientRequestDto clientAndUserClientRequestDto);
}

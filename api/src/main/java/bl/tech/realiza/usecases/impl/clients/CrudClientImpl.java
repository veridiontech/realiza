package bl.tech.realiza.usecases.impl.clients;

import bl.tech.realiza.gateways.requests.clients.ClientRequestDto;
import bl.tech.realiza.gateways.responses.clients.ClientResponseDto;
import bl.tech.realiza.usecases.interfaces.clients.CrudClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CrudClientImpl implements CrudClient {
    @Override
    public ClientResponseDto save(ClientRequestDto clientRequestDto) {
        return null;
    }

    @Override
    public Optional<ClientResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<ClientResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<ClientResponseDto> update(ClientRequestDto clientRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}

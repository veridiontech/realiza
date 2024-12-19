package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.gateways.repositories.users.UserClientRepository;
import bl.tech.realiza.gateways.requests.users.UserClientRequestDto;
import bl.tech.realiza.gateways.responses.users.UserClientResponseDto;
import bl.tech.realiza.usecases.interfaces.users.CrudUserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudUserClientImpl implements CrudUserClient {

    private final UserClientRepository userClientRepository;

    @Override
    public UserClientResponseDto save(UserClientRequestDto userClientRequestDto) {
        return null;
    }

    @Override
    public Optional<UserClientResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<UserClientResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<UserClientResponseDto> update(UserClientRequestDto userClientRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}

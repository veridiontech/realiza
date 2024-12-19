package bl.tech.realiza.usecases.interfaces.users;

import bl.tech.realiza.gateways.requests.users.UserClientRequestDto;
import bl.tech.realiza.gateways.responses.users.UserClientResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudUserClient {
    UserClientResponseDto save(UserClientRequestDto userClientRequestDto);
    Optional<UserClientResponseDto> findOne(String id);
    Page<UserClientResponseDto> findAll(Pageable pageable);
    Optional<UserClientResponseDto> update(String id, UserClientRequestDto userClientRequestDto);
    void delete(String id);
}

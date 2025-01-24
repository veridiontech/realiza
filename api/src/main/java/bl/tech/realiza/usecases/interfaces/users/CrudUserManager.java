package bl.tech.realiza.usecases.interfaces.users;

import bl.tech.realiza.gateways.requests.users.UserManagerRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudUserManager {
    UserResponseDto save(UserManagerRequestDto userManagerRequestDto);
    Optional<UserResponseDto> findOne(String id);
    Page<UserResponseDto> findAll(Pageable pageable);
    Optional<UserResponseDto> update(String id, UserManagerRequestDto userManagerRequestDto);
    void delete(String id);
    String changePassword(String id, UserManagerRequestDto userManagerRequestDto);
}

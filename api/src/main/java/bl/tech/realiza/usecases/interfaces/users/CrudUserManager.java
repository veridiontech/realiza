package bl.tech.realiza.usecases.interfaces.users;

import bl.tech.realiza.gateways.requests.users.UserCreateRequestDto;
import bl.tech.realiza.gateways.requests.users.UserManagerRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface CrudUserManager {
    UserResponseDto save(UserManagerRequestDto userManagerRequestDto, MultipartFile file) throws IOException;
    Optional<UserResponseDto> findOne(String id);
    Page<UserResponseDto> findAll(Pageable pageable);
    Optional<UserResponseDto> update(String id, UserManagerRequestDto userManagerRequestDto);
    void delete(String id);
    String changePassword(String id, UserManagerRequestDto userManagerRequestDto);
    String changeProfilePicture(String id, MultipartFile file) throws IOException;
    String createNewUserActivated(UserCreateRequestDto userCreateRequestDto);
}

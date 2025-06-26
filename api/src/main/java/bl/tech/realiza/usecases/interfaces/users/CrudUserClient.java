package bl.tech.realiza.usecases.interfaces.users;

import bl.tech.realiza.gateways.requests.users.UserClientRequestDto;
import bl.tech.realiza.gateways.requests.users.UserProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.requests.users.UserProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderResponseDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface CrudUserClient {
    UserResponseDto save(UserClientRequestDto userClientRequestDto);
    Optional<UserResponseDto> findOne(String id);
    Page<UserResponseDto> findAll(Pageable pageable);
    Optional<UserResponseDto> update(String id, UserClientRequestDto userClientRequestDto);
    void delete(String id);
    Page<UserResponseDto> findAllByClient(String idSearch, Pageable pageable);
    Page<UserResponseDto> findAllInnactiveAndActiveByClient(String idSearch, Pageable pageable);
    String changePassword(String id, UserClientRequestDto userClientRequestDto);
    String changeProfilePicture(String id, MultipartFile file) throws IOException;
}

package bl.tech.realiza.usecases.interfaces.users;

import bl.tech.realiza.gateways.requests.users.UserProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface CrudUserProviderSupplier {
    UserResponseDto save(UserProviderSupplierRequestDto userProviderSupplierRequestDto);
    Optional<UserResponseDto> findOne(String id);
    Page<UserResponseDto> findAll(Pageable pageable);
    Optional<UserResponseDto> update(String id, UserProviderSupplierRequestDto userProviderSupplierRequestDto);
    void delete(String id);
    Page<UserResponseDto> findAllBySupplier(String idSearch, Pageable pageable);
    String changePassword(String id, UserProviderSupplierRequestDto userProviderSupplierRequestDto);
    String changeProfilePicture(String id, MultipartFile file) throws IOException;
}

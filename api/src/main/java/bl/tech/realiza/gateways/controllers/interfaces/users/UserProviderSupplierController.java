package bl.tech.realiza.gateways.controllers.interfaces.users;

import bl.tech.realiza.gateways.requests.users.UserProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserProviderSupplierController {
    ResponseEntity<UserResponseDto> createUserSupplier(UserProviderSupplierRequestDto userProviderSupplierRequestDto);
    ResponseEntity<Optional<UserResponseDto>> getOneUserSupplier(String id);
    ResponseEntity<Page<UserResponseDto>> getAllUserSuppliers(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<UserResponseDto>> updateUserSupplier(String id, UserProviderSupplierRequestDto userProviderSupplierRequestDto);
    ResponseEntity<Void> deleteUserSupplier(String id);
    ResponseEntity<Page<UserResponseDto>> getAllUserSuppliersBySupplier(int page, int size, String sort, Sort.Direction direction, String idSearch);
}

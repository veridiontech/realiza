package bl.tech.realiza.gateways.controllers.interfaces.users;

import bl.tech.realiza.gateways.requests.users.UserSupplierRequestDto;
import bl.tech.realiza.gateways.responses.users.UserSupplierResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserSupplierController {
    ResponseEntity<UserSupplierResponseDto> createUserSupplier(UserSupplierRequestDto branchRequestDto);
    ResponseEntity<Optional<UserSupplierResponseDto>> getOneUserSupplier(String id);
    ResponseEntity<Page<UserSupplierResponseDto>> getAllUserSuppliers(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<UserSupplierResponseDto>> updateUserSupplier(UserSupplierRequestDto branchRequestDto);
    ResponseEntity<Void> deleteUserSupplier(String id);
}

package bl.tech.realiza.gateways.controllers.impl.users;

import bl.tech.realiza.gateways.controllers.interfaces.users.UserSupplierController;
import bl.tech.realiza.gateways.requests.users.UserSupplierRequestDto;
import bl.tech.realiza.gateways.responses.users.UserSupplierResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class UserSupplierControllerImpl implements UserSupplierController {
    @Override
    public ResponseEntity<UserSupplierResponseDto> createUserSupplier(UserSupplierRequestDto branchRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<UserSupplierResponseDto>> getOneUserSupplier(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Page<UserSupplierResponseDto>> getAllUserSuppliers(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<UserSupplierResponseDto>> updateUserSupplier(UserSupplierRequestDto branchRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteUserSupplier(String id) {
        return null;
    }
}

package bl.tech.realiza.usecases.interfaces.users;

import bl.tech.realiza.gateways.requests.users.UserSupplierRequestDto;
import bl.tech.realiza.gateways.responses.users.UserSupplierResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudUserSupplier {
    UserSupplierResponseDto save(UserSupplierRequestDto userSupplierRequestDto);
    Optional<UserSupplierResponseDto> findOne(String id);
    Page<UserSupplierResponseDto> findAll(Pageable pageable);
    Optional<UserSupplierResponseDto> update(UserSupplierRequestDto userSupplierRequestDto);
    void delete(String id);
}

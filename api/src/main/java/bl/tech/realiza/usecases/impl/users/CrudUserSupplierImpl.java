package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.gateways.requests.users.UserSupplierRequestDto;
import bl.tech.realiza.gateways.responses.users.UserSupplierResponseDto;
import bl.tech.realiza.usecases.interfaces.users.CrudUserSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CrudUserSupplierImpl implements CrudUserSupplier {
    @Override
    public UserSupplierResponseDto save(UserSupplierRequestDto userSupplierRequestDto) {
        return null;
    }

    @Override
    public Optional<UserSupplierResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<UserSupplierResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<UserSupplierResponseDto> update(UserSupplierRequestDto userSupplierRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}

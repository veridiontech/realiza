package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.gateways.requests.users.UserClientRequestDto;
import bl.tech.realiza.gateways.requests.users.UserProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.requests.users.UserProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.usecases.interfaces.users.CrudUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class CrudUserImpl implements CrudUser {
    @Override
    public UserResponseDto saveClient(UserClientRequestDto userClientRequestDto) {
        return null;
    }

    @Override
    public UserResponseDto saveSubcontractor(UserProviderSubcontractorRequestDto userProviderSubcontractorRequestDto) {
        return null;
    }

    @Override
    public UserResponseDto saveSupplier(UserProviderSupplierRequestDto userProviderSupplierRequestDto) {
        return null;
    }

    @Override
    public Optional<UserResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<UserResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<UserResponseDto> updateClient(UserClientRequestDto userClientRequestDto) {
        return Optional.empty();
    }

    @Override
    public Optional<UserResponseDto> updateSubcontractor(UserProviderSubcontractorRequestDto userProviderSubcontractorRequestDto) {
        return Optional.empty();
    }

    @Override
    public Optional<UserResponseDto> updateSupplier(UserProviderSupplierRequestDto userProviderSupplierRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}

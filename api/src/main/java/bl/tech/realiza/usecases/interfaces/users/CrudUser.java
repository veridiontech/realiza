package bl.tech.realiza.usecases.interfaces.users;

import bl.tech.realiza.gateways.requests.users.UserClientRequestDto;
import bl.tech.realiza.gateways.requests.users.UserProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.requests.users.UserProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudUser {
    UserResponseDto saveClient(UserClientRequestDto userClientRequestDto);
    UserResponseDto saveSubcontractor(UserProviderSubcontractorRequestDto userProviderSubcontractorRequestDto);
    UserResponseDto saveSupplier(UserProviderSupplierRequestDto userProviderSupplierRequestDto);
    Optional<UserResponseDto> findOne(String id);
    Page<UserResponseDto> findAll(Pageable pageable);
    Optional<UserResponseDto> updateClient(UserClientRequestDto userClientRequestDto);
    Optional<UserResponseDto> updateSubcontractor(UserProviderSubcontractorRequestDto userProviderSubcontractorRequestDto);
    Optional<UserResponseDto> updateSupplier(UserProviderSupplierRequestDto userProviderSupplierRequestDto);
    void delete(String id);
}

package bl.tech.realiza.usecases.interfaces.contracts;

import bl.tech.realiza.gateways.requests.contracts.ContractRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudContractProviderSupplier {
    ContractResponseDto save(ContractRequestDto contractProviderSupplierRequestDto);
    Optional<ContractResponseDto> findOne(String id);
    Page<ContractResponseDto> findAll(Pageable pageable);
    Optional<ContractResponseDto> update(ContractRequestDto contractProviderSupplierRequestDto);
    void delete(String id);
    Page<ContractResponseDto> findAllBySupplier(String idSearch, Pageable pageable);
}

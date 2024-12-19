package bl.tech.realiza.usecases.interfaces.contracts;

import bl.tech.realiza.gateways.requests.contracts.ContractSupplierRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractSupplierResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudSupplier {
    ContractSupplierResponseDto save(ContractSupplierRequestDto contractSupplierRequestDto);
    Optional<ContractSupplierResponseDto> findOne(String id);
    Page<ContractSupplierResponseDto> findAll(Pageable pageable);
    Optional<ContractSupplierResponseDto> update(String id, ContractSupplierRequestDto contractSupplierRequestDto);
    void delete(String id);
}

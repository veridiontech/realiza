package bl.tech.realiza.usecases.interfaces.contracts.contract;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.gateways.requests.contracts.ContractAndSupplierCreateRequestDto;
import bl.tech.realiza.gateways.requests.contracts.ContractRequestDto;
import bl.tech.realiza.gateways.requests.contracts.ContractSupplierPostRequestDto;
import bl.tech.realiza.gateways.responses.contracts.contract.ContractAndSupplierCreateResponseDto;
import bl.tech.realiza.gateways.responses.contracts.contract.ContractResponseDto;
import bl.tech.realiza.gateways.responses.contracts.contract.ContractSupplierPermissionResponseDto;
import bl.tech.realiza.gateways.responses.contracts.contract.ContractSupplierResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CrudContractProviderSupplier {
    ContractSupplierResponseDto save(ContractSupplierPostRequestDto contractProviderSupplierRequestDto);
    Optional<ContractResponseDto> findOne(String id);
    Page<ContractResponseDto> findAll(Pageable pageable);
    Optional<ContractResponseDto> update(String id, ContractRequestDto contractProviderSupplierRequestDto);
    void delete(String id);
    Page<ContractResponseDto> findAllBySupplier(String idSearch, List<Contract.IsActive> isActive, Pageable pageable);
    Page<ContractResponseDto> findAllByClient(String idSearch, List<Contract.IsActive> isActive, Pageable pageable);
    Page<ContractResponseDto> findAllBySupplierAndBranch(String idSupplier, String idBranch, Pageable pageable);
    ContractAndSupplierCreateResponseDto saveContractAndSupplier(ContractAndSupplierCreateRequestDto contractAndSupplierCreateRequestDto);
    List<ContractSupplierPermissionResponseDto> findAllByBranchAndSubcontractPermission(String idBranch);

}

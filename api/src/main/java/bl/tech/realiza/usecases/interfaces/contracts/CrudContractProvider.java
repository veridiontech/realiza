package bl.tech.realiza.usecases.interfaces.contracts;

import bl.tech.realiza.gateways.requests.contracts.ContractProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.requests.contracts.ContractProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractProviderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudContractProvider {
    ContractProviderResponseDto saveSubcontractor(ContractProviderSubcontractorRequestDto contractProviderSubcontractorRequestDto);
    ContractProviderResponseDto saveSupplier(ContractProviderSupplierRequestDto contractProviderSupplierRequestDto);
    Optional<ContractProviderResponseDto> findOne(String id);
    Page<ContractProviderResponseDto> findAll(Pageable pageable);
    Optional<ContractProviderResponseDto> updateSubcontractor(ContractProviderSubcontractorRequestDto contractProviderSubcontractorRequestDto);
    Optional<ContractProviderResponseDto> updateSupplier(ContractProviderSupplierRequestDto contractProviderSupplierRequestDto);
    void delete(String id);
}

package bl.tech.realiza.usecases.interfaces.contracts;

import bl.tech.realiza.gateways.requests.contracts.ContractProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.requests.contracts.ContractProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractProviderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudContractProviderSubcontractor {
    ContractProviderResponseDto save(ContractProviderSubcontractorRequestDto contractProviderSubcontractorRequestDto);
    Optional<ContractProviderResponseDto> findOne(String id);
    Page<ContractProviderResponseDto> findAll(Pageable pageable);
    Optional<ContractProviderResponseDto> update(ContractProviderSubcontractorRequestDto contractProviderSubcontractorRequestDto);
    void delete(String id);
}

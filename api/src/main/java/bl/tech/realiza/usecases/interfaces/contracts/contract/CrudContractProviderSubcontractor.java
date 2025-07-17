package bl.tech.realiza.usecases.interfaces.contracts.contract;

import bl.tech.realiza.gateways.requests.contracts.ContractRequestDto;
import bl.tech.realiza.gateways.requests.contracts.ContractSubcontractorPostRequestDto;
import bl.tech.realiza.gateways.responses.contracts.contract.ContractSubcontractorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CrudContractProviderSubcontractor {
    ContractSubcontractorResponseDto save(ContractSubcontractorPostRequestDto contractRequestDto);
    Optional<ContractSubcontractorResponseDto> findOne(String id);
    Page<ContractSubcontractorResponseDto> findAll(Pageable pageable);
    Optional<ContractSubcontractorResponseDto> update(String id, ContractRequestDto contractRequestDto);
    void delete(String id);
    Page<ContractSubcontractorResponseDto> findAllBySubcontractor(String idSearch, Pageable pageable);
    Page<ContractSubcontractorResponseDto> findAllBySupplier(String idSearch, Pageable pageable);
    List<ContractSubcontractorResponseDto> findAllByContractSupplier(String contractId);
}

package bl.tech.realiza.usecases.interfaces.contracts;

import bl.tech.realiza.gateways.requests.contracts.ContractRequestDto;
import bl.tech.realiza.gateways.requests.contracts.ContractSubcontractorPostRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractResponseDto;
import bl.tech.realiza.gateways.responses.contracts.ContractSubcontractorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudContractProviderSubcontractor {
    ContractSubcontractorResponseDto save(ContractSubcontractorPostRequestDto contractRequestDto);
    Optional<ContractResponseDto> findOne(String id);
    Page<ContractResponseDto> findAll(Pageable pageable);
    Optional<ContractResponseDto> update(String id, ContractRequestDto contractRequestDto);
    void delete(String id);
    Page<ContractResponseDto> findAllBySubcontractor(String idSearch, Pageable pageable);
    Page<ContractResponseDto> findAllBySupplier(String idSearch, Pageable pageable);
}

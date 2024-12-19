package bl.tech.realiza.usecases.interfaces.contracts;

import bl.tech.realiza.gateways.requests.contracts.ContractSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractSubcontractorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudSubcontractor {
    ContractSubcontractorResponseDto save(ContractSubcontractorRequestDto contractSubcontractorRequestDto);
    Optional<ContractSubcontractorResponseDto> findOne(String id);
    Page<ContractSubcontractorResponseDto> findAll(Pageable pageable);
    Optional<ContractSubcontractorResponseDto> update(String id, ContractSubcontractorRequestDto contractSubcontractorRequestDto);
    void delete(String id);
}

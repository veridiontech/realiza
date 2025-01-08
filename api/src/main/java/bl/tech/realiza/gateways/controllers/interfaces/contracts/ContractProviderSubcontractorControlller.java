package bl.tech.realiza.gateways.controllers.interfaces.contracts;

import bl.tech.realiza.gateways.requests.contracts.ContractProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractProviderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface ContractProviderSubcontractorControlller {
    ResponseEntity<ContractProviderResponseDto> createContractProviderSubcontractor(ContractProviderSubcontractorRequestDto contractProviderSubcontractorRequestDto);
    ResponseEntity<Optional<ContractProviderResponseDto>> getOneContractProviderSubcontractor(String id);
    ResponseEntity<Page<ContractProviderResponseDto>> getAllContractsProviderSubcontractor(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<ContractProviderResponseDto>> updateContractProviderSubcontractor(ContractProviderSubcontractorRequestDto contractProviderSubcontractorRequestDto);
    ResponseEntity<Void> deleteContractProviderSubcontractor(String id);
}

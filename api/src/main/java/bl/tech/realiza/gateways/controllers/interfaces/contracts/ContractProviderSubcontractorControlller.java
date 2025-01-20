package bl.tech.realiza.gateways.controllers.interfaces.contracts;

import bl.tech.realiza.gateways.requests.contracts.ContractRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface ContractProviderSubcontractorControlller {
    ResponseEntity<ContractResponseDto> createContractProviderSubcontractor(ContractRequestDto contractRequestDto);
    ResponseEntity<Optional<ContractResponseDto>> getOneContractProviderSubcontractor(String id);
    ResponseEntity<Page<ContractResponseDto>> getAllContractsProviderSubcontractor(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<ContractResponseDto>> updateContractProviderSubcontractor(ContractRequestDto contractRequestDto);
    ResponseEntity<Void> deleteContractProviderSubcontractor(String id);
}

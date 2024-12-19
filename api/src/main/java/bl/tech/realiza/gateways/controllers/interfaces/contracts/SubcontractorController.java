package bl.tech.realiza.gateways.controllers.interfaces.contracts;

import bl.tech.realiza.gateways.requests.contracts.ContractSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractSubcontractorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface SubcontractorController {
    ResponseEntity<ContractSubcontractorResponseDto> createContractSubcontractor(ContractSubcontractorRequestDto contractSubcontractorRequestDto);
    ResponseEntity<Optional<ContractSubcontractorResponseDto>> getOneContractSubcontractor(String id);
    ResponseEntity<Page<ContractSubcontractorResponseDto>> getAllSubcontractors(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<ContractSubcontractorResponseDto>> updateContractSubcontractor(ContractSubcontractorRequestDto contractSubcontractorRequestDto);
    ResponseEntity<Void> deleteContractSubcontractor(String id);
}

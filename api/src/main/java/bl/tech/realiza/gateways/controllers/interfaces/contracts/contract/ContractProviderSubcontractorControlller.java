package bl.tech.realiza.gateways.controllers.interfaces.contracts.contract;

import bl.tech.realiza.gateways.requests.contracts.ContractRequestDto;
import bl.tech.realiza.gateways.requests.contracts.ContractSubcontractorPostRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractResponseDto;
import bl.tech.realiza.gateways.responses.contracts.ContractSubcontractorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface ContractProviderSubcontractorControlller {
    ResponseEntity<ContractSubcontractorResponseDto> createContractProviderSubcontractor(ContractSubcontractorPostRequestDto contractRequestDto);
    ResponseEntity<Optional<ContractResponseDto>> getOneContractProviderSubcontractor(String id);
    ResponseEntity<Page<ContractResponseDto>> getAllContractsProviderSubcontractor(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<ContractResponseDto>> updateContractProviderSubcontractor(String id, ContractRequestDto contractRequestDto);
    ResponseEntity<Void> deleteContractProviderSubcontractor(String id);
    ResponseEntity<Page<ContractResponseDto>> getAllContractsProviderSubcontractorBySupplier(int page, int size, String sort, Sort.Direction direction, String idSearch);
    ResponseEntity<Page<ContractResponseDto>> getAllBySupplier(int page, int size, String sort, Sort.Direction direction, String idSupplier);
}

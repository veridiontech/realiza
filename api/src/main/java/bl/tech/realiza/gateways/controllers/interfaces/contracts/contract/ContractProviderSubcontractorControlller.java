package bl.tech.realiza.gateways.controllers.interfaces.contracts.contract;

import bl.tech.realiza.gateways.requests.contracts.ContractRequestDto;
import bl.tech.realiza.gateways.requests.contracts.ContractSubcontractorPostRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractSubcontractorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface ContractProviderSubcontractorControlller {
    ResponseEntity<ContractSubcontractorResponseDto> createContractProviderSubcontractor(ContractSubcontractorPostRequestDto contractRequestDto);
    ResponseEntity<Optional<ContractSubcontractorResponseDto>> getOneContractProviderSubcontractor(String id);
    ResponseEntity<Page<ContractSubcontractorResponseDto>> getAllContractsProviderSubcontractor(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<ContractSubcontractorResponseDto>> updateContractProviderSubcontractor(String id, ContractRequestDto contractRequestDto);
    ResponseEntity<Void> deleteContractProviderSubcontractor(String id);
    ResponseEntity<Page<ContractSubcontractorResponseDto>> getAllContractsProviderSubcontractorBySupplier(int page, int size, String sort, Sort.Direction direction, String idSearch);
    ResponseEntity<Page<ContractSubcontractorResponseDto>> getAllBySupplier(int page, int size, String sort, Sort.Direction direction, String idSupplier);
}

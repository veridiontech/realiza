package bl.tech.realiza.gateways.controllers.interfaces.contracts;

import bl.tech.realiza.gateways.requests.contracts.ContractAndSupplierCreateRequestDto;
import bl.tech.realiza.gateways.requests.contracts.ContractRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractAndSupplierCreateResponseDto;
import bl.tech.realiza.gateways.responses.contracts.ContractResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface ContractProviderSupplierControlller {
    ResponseEntity<ContractResponseDto> createContractProviderSupplier(ContractRequestDto contractProviderSupplierRequestDto);
    ResponseEntity<Optional<ContractResponseDto>> getOneContractProviderSupplier(String id);
    ResponseEntity<Page<ContractResponseDto>> getAllContractsProviderSupplier(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<ContractResponseDto>> updateContractProviderSupplier(String id, ContractRequestDto contractProviderSupplierRequestDto);
    ResponseEntity<Void> deleteContractProviderSupplier(String id);
    ResponseEntity<Page<ContractResponseDto>> getAllContractsProviderSupplierBySupplier(int page, int size, String sort, Sort.Direction direction, String idSearch);
    ResponseEntity<Page<ContractResponseDto>> getAllContractsProviderSupplierByClient(int page, int size, String sort, Sort.Direction direction, String idSearch);
    ResponseEntity<Page<ContractResponseDto>> getAllBySupplierAndBranch(int page, int size, String sort, Sort.Direction direction, String idBranch, String idSupplier);
    ResponseEntity<ContractAndSupplierCreateResponseDto> createContractAndSupplier(ContractAndSupplierCreateRequestDto contractAndSupplierCreateRequestDto);
}

package bl.tech.realiza.gateways.controllers.interfaces.contracts;

import bl.tech.realiza.gateways.requests.contracts.ContractProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractProviderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface ContractProviderSupplierControlller {
    ResponseEntity<ContractProviderResponseDto> createContractProviderSupplier(ContractProviderSupplierRequestDto contractProviderSupplierRequestDto);
    ResponseEntity<Optional<ContractProviderResponseDto>> getOneContractProviderSupplier(String id);
    ResponseEntity<Page<ContractProviderResponseDto>> getAllContractsProviderSupplier(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<ContractProviderResponseDto>> updateContractProviderSupplier(ContractProviderSupplierRequestDto contractProviderSupplierRequestDto);
    ResponseEntity<Void> deleteContractProviderSupplier(String id);
}

package bl.tech.realiza.gateways.controllers.interfaces.contracts;

import bl.tech.realiza.gateways.requests.contracts.ContractSupplierRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractSupplierResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface SupplierController {
    ResponseEntity<ContractSupplierResponseDto> createContractSupplier(ContractSupplierRequestDto contractSupplierRequestDto);
    ResponseEntity<Optional<ContractSupplierResponseDto>> getOneContractSupplier(String id);
    ResponseEntity<Page<ContractSupplierResponseDto>> getAllSuppliers(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<ContractSupplierResponseDto>> updateContractSupplier(ContractSupplierRequestDto contractSupplierRequestDto);
    ResponseEntity<Void> deleteContractSupplier(String id);
}

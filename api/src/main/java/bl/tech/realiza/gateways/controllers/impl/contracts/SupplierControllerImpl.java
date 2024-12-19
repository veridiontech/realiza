package bl.tech.realiza.gateways.controllers.impl.contracts;

import bl.tech.realiza.gateways.controllers.interfaces.contracts.SupplierController;
import bl.tech.realiza.gateways.requests.contracts.ContractSupplierRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractSupplierResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class SupplierControllerImpl implements SupplierController {
    @Override
    public ResponseEntity<ContractSupplierResponseDto> createContractSupplier(ContractSupplierRequestDto contractSupplierRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<ContractSupplierResponseDto>> getOneContractSupplier(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Page<ContractSupplierResponseDto>> getAllSuppliers(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<ContractSupplierResponseDto>> updateContractSupplier(ContractSupplierRequestDto contractSupplierRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteContractSupplier(String id) {
        return null;
    }
}

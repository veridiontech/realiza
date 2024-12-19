package bl.tech.realiza.gateways.controllers.impl.contracts;

import bl.tech.realiza.gateways.controllers.interfaces.contracts.SupplierController;
import bl.tech.realiza.gateways.requests.contracts.ContractSupplierRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractSupplierResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/supplier")
public class SupplierControllerImpl implements SupplierController {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ContractSupplierResponseDto> createContractSupplier(ContractSupplierRequestDto contractSupplierRequestDto) {
        return null;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ContractSupplierResponseDto>> getOneContractSupplier(String id) {
        return null;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ContractSupplierResponseDto>> getAllSuppliers(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ContractSupplierResponseDto>> updateContractSupplier(ContractSupplierRequestDto contractSupplierRequestDto) {
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteContractSupplier(String id) {
        return null;
    }
}

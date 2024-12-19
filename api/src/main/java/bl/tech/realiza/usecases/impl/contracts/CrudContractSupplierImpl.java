package bl.tech.realiza.usecases.impl.contracts;

import bl.tech.realiza.gateways.repositories.contracts.ContractSupplierRepository;
import bl.tech.realiza.gateways.requests.contracts.ContractSupplierRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractSupplierResponseDto;
import bl.tech.realiza.usecases.interfaces.contracts.CrudSupplier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudContractSupplierImpl implements CrudSupplier {

    private final ContractSupplierRepository contractSupplierRepository;

    @Override
    public ContractSupplierResponseDto save(ContractSupplierRequestDto contractSupplierRequestDto) {
        return null;
    }

    @Override
    public Optional<ContractSupplierResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<ContractSupplierResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<ContractSupplierResponseDto> update(ContractSupplierRequestDto contractSupplierRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}

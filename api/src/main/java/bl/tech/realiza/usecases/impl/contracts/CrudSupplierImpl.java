package bl.tech.realiza.usecases.impl.contracts;

import bl.tech.realiza.gateways.requests.contracts.ContractSupplierRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractSupplierResponseDto;
import bl.tech.realiza.usecases.interfaces.contracts.CrudSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CrudSupplierImpl implements CrudSupplier {
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
    public Optional<ContractSupplierResponseDto> update(String id, ContractSupplierRequestDto contractSupplierRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}

package bl.tech.realiza.usecases.impl.contracts;

import bl.tech.realiza.gateways.requests.contracts.ContractProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.requests.contracts.ContractProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractProviderResponseDto;
import bl.tech.realiza.usecases.interfaces.contracts.CrudContractProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class CrudContractProviderImpl implements CrudContractProvider {
    @Override
    public ContractProviderResponseDto saveSubcontractor(ContractProviderSubcontractorRequestDto contractProviderSubcontractorRequestDto) {
        return null;
    }

    @Override
    public ContractProviderResponseDto saveSupplier(ContractProviderSupplierRequestDto contractProviderSupplierRequestDto) {
        return null;
    }

    @Override
    public Optional<ContractProviderResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<ContractProviderResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<ContractProviderResponseDto> updateSubcontractor(ContractProviderSubcontractorRequestDto contractProviderSubcontractorRequestDto) {
        return Optional.empty();
    }

    @Override
    public Optional<ContractProviderResponseDto> updateSupplier(ContractProviderSupplierRequestDto contractProviderSupplierRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}

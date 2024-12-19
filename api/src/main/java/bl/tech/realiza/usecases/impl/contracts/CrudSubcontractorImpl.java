package bl.tech.realiza.usecases.impl.contracts;

import bl.tech.realiza.gateways.requests.contracts.ContractSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractSubcontractorResponseDto;
import bl.tech.realiza.usecases.interfaces.contracts.CrudSubcontractor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CrudSubcontractorImpl implements CrudSubcontractor {
    @Override
    public ContractSubcontractorResponseDto save(ContractSubcontractorRequestDto contractSubcontractorRequestDto) {
        return null;
    }

    @Override
    public Optional<ContractSubcontractorResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<ContractSubcontractorResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<ContractSubcontractorResponseDto> update(ContractSubcontractorRequestDto contractSubcontractorRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}

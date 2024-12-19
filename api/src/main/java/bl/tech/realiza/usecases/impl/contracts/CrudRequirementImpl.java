package bl.tech.realiza.usecases.impl.contracts;

import bl.tech.realiza.gateways.requests.contracts.RequirementRequestDto;
import bl.tech.realiza.gateways.responses.contracts.RequirementResponseDto;
import bl.tech.realiza.usecases.interfaces.contracts.CrudRequirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CrudRequirementImpl implements CrudRequirement {
    @Override
    public RequirementResponseDto save(RequirementRequestDto requirementRequestDto) {
        return null;
    }

    @Override
    public Optional<RequirementResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<RequirementResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<RequirementResponseDto> update(RequirementRequestDto requirementRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}

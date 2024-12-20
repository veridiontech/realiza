package bl.tech.realiza.usecases.impl.contracts;

import bl.tech.realiza.domains.contracts.Requirement;
import bl.tech.realiza.gateways.repositories.contracts.RequirementRepository;
import bl.tech.realiza.gateways.requests.contracts.RequirementRequestDto;
import bl.tech.realiza.gateways.responses.contracts.RequirementResponseDto;
import bl.tech.realiza.usecases.interfaces.contracts.CrudRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudRequirementImpl implements CrudRequirement {

    private final RequirementRepository requirementRepository;

    @Override
    public RequirementResponseDto save(RequirementRequestDto requirementRequestDto) {

        Requirement requirement = Requirement.builder()
                .title(requirementRequestDto.getTitle())
                .build();

        Requirement savedRequirement = requirementRepository.save(requirement);

        RequirementResponseDto requirementResponse = RequirementResponseDto.builder()
                .idRequirement(savedRequirement.getIdRequirement())
                .title(savedRequirement.getTitle())
                .build();

        return requirementResponse;
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

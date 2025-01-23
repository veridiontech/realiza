package bl.tech.realiza.usecases.impl.contracts;

import bl.tech.realiza.domains.contract.Requirement;
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
        Optional<Requirement> requirementOptional = requirementRepository.findById(id);

        Requirement requirement = requirementOptional.orElseThrow(() -> new RuntimeException("Requirement not found"));

        RequirementResponseDto requirementResponse = RequirementResponseDto.builder()
                .idRequirement(requirement.getIdRequirement())
                .title(requirement.getTitle())
                .build();

        return Optional.of(requirementResponse);
    }

    @Override
    public Page<RequirementResponseDto> findAll(Pageable pageable) {
        Page<Requirement> requirementPage = requirementRepository.findAll(pageable);

        Page<RequirementResponseDto> requirementResponseDtoPage = requirementPage.map(
                requirement -> RequirementResponseDto.builder()
                        .idRequirement(requirement.getIdRequirement())
                        .title(requirement.getTitle())
                        .build()
        );

        return requirementResponseDtoPage;
    }

    @Override
    public Optional<RequirementResponseDto> update(String id, RequirementRequestDto requirementRequestDto) {
        Optional<Requirement> requirementOptional = requirementRepository.findById(id);

        Requirement requirement = requirementOptional.orElseThrow(() -> new RuntimeException("Requirement not found"));

        requirement.setTitle(requirementRequestDto.getTitle() != null ? requirementRequestDto.getTitle() : requirement.getTitle());
        requirement.setIsActive(requirementRequestDto.getIsActive() != null ? requirementRequestDto.getIsActive() : requirement.getIsActive());

        Requirement savedRequirement = requirementRepository.save(requirement);

        RequirementResponseDto requirementResponse = RequirementResponseDto.builder()
                .idRequirement(savedRequirement.getIdRequirement())
                .title(savedRequirement.getTitle())
                .build();

        return Optional.of(requirementResponse);
    }

    @Override
    public void delete(String id) {
        requirementRepository.deleteById(id);
    }
}

package bl.tech.realiza.usecases.interfaces.contracts;

import bl.tech.realiza.gateways.requests.contracts.RequirementRequestDto;
import bl.tech.realiza.gateways.responses.contracts.RequirementResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudRequirement {
    RequirementResponseDto save(RequirementRequestDto requirementRequestDto);
    Optional<RequirementResponseDto> findOne(String id);
    Page<RequirementResponseDto> findAll(Pageable pageable);
    Optional<RequirementResponseDto> update(RequirementRequestDto requirementRequestDto);
    void delete(String id);
}

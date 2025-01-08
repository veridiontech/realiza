package bl.tech.realiza.gateways.controllers.interfaces.contracts;

import bl.tech.realiza.gateways.requests.contracts.RequirementRequestDto;
import bl.tech.realiza.gateways.responses.contracts.RequirementResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface RequirementControlller {
    ResponseEntity<RequirementResponseDto> createRequirement(RequirementRequestDto requirementRequestDto);
    ResponseEntity<Optional<RequirementResponseDto>> getOneRequirement(String id);
    ResponseEntity<Page<RequirementResponseDto>> getAllRequirements(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<RequirementResponseDto>> updateRequirement(RequirementRequestDto requirementRequestDto);
    ResponseEntity<Void> deleteRequirement(String id);
}

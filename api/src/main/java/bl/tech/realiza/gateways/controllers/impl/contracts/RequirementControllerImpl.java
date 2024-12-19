package bl.tech.realiza.gateways.controllers.impl.contracts;

import bl.tech.realiza.gateways.controllers.interfaces.contracts.RequirementController;
import bl.tech.realiza.gateways.requests.contracts.RequirementRequestDto;
import bl.tech.realiza.gateways.responses.contracts.RequirementResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class RequirementControllerImpl implements RequirementController {
    @Override
    public ResponseEntity<RequirementResponseDto> createRequirement(RequirementRequestDto requirementRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<RequirementResponseDto>> getOneRequirement(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Page<RequirementResponseDto>> getAllRequirements(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<RequirementResponseDto>> updateRequirement(RequirementRequestDto requirementRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteRequirement(String id) {
        return null;
    }
}

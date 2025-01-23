package bl.tech.realiza.gateways.controllers.impl.contracts;

import bl.tech.realiza.gateways.controllers.interfaces.contracts.RequirementControlller;
import bl.tech.realiza.gateways.requests.contracts.RequirementRequestDto;
import bl.tech.realiza.gateways.responses.contracts.RequirementResponseDto;
import bl.tech.realiza.usecases.impl.contracts.CrudRequirementImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contract/requirement")
@Tag(name = "Requirement")
public class RequirementControllerImpl implements RequirementControlller {

    private final CrudRequirementImpl crudRequirement;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<RequirementResponseDto> createRequirement(@RequestBody @Valid RequirementRequestDto requirementRequestDto) {
        RequirementResponseDto requirement = crudRequirement.save(requirementRequestDto);

        return ResponseEntity.of(Optional.of(requirement));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<RequirementResponseDto>> getOneRequirement(@PathVariable String id) {
        Optional<RequirementResponseDto> requirement = crudRequirement.findOne(id);

        return ResponseEntity.of(Optional.of(requirement));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<RequirementResponseDto>> getAllRequirements(@RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "5") int size,
                                                                           @RequestParam(defaultValue = "idRequirement") String sort,
                                                                           @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<RequirementResponseDto> pageRequirements = crudRequirement.findAll(pageable);

        return ResponseEntity.ok(pageRequirements);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<RequirementResponseDto>> updateRequirement(@PathVariable String id, @RequestBody @Valid RequirementRequestDto requirementRequestDto) {
        Optional<RequirementResponseDto> requirement = crudRequirement.update(id, requirementRequestDto);

        return ResponseEntity.of(Optional.of(requirement));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteRequirement(@PathVariable String id) {
        crudRequirement.delete(id);

        return null;
    }
}

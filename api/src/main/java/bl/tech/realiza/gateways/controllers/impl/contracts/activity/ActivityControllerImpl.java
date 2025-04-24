package bl.tech.realiza.gateways.controllers.impl.contracts.activity;

import bl.tech.realiza.gateways.controllers.interfaces.contracts.ActivityControlller;
import bl.tech.realiza.gateways.requests.contracts.ActivityRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ActivityDocumentResponseDto;
import bl.tech.realiza.gateways.responses.contracts.ActivityResponseDto;
import bl.tech.realiza.usecases.impl.contracts.CrudActivityImpl;
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

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contract/activity")
@Tag(name = "Activity", description = "Alter the branch activities")
public class ActivityControllerImpl implements ActivityControlller {

    private final CrudActivityImpl crudActivity;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ActivityResponseDto> createActivity(@RequestBody @Valid ActivityRequestDto activityRequestDto) {
        ActivityResponseDto activity = crudActivity.save(activityRequestDto);

        return ResponseEntity.of(Optional.of(activity));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ActivityResponseDto>> getOneActivity(@PathVariable String id) {
        Optional<ActivityResponseDto> activity = crudActivity.findOne(id);

        return ResponseEntity.of(Optional.of(activity));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ActivityResponseDto>> getAllActivities(@RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "5") int size,
                                                                      @RequestParam(defaultValue = "idActivity") String sort,
                                                                      @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ActivityResponseDto> pageActivity = crudActivity.findAll(pageable);

        return ResponseEntity.ok(pageActivity);
    }

    @GetMapping("/find-by-branch/{idBranch}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<List<ActivityResponseDto>> getAllActivitiesByBranch(@PathVariable String idBranch) {
        return ResponseEntity.of(Optional.of(crudActivity.findAllByBranch(idBranch)));
    }

    @GetMapping("/find-document-by-activity/{idActivity}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<List<ActivityDocumentResponseDto>> getAllDocumentsByActivity(@PathVariable String idActivity) {
        return ResponseEntity.ok(crudActivity.findAllDocumentsByActivity(idActivity));
    }

    @PostMapping("/add-document-to-activity/{idActivity}")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ActivityDocumentResponseDto> addDocumentsToActivity(@PathVariable String idActivity, @RequestParam String idDocumentBranch) {
        return ResponseEntity.ok(crudActivity.addDocumentToActivity(idActivity, idDocumentBranch));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ActivityResponseDto>> updateActivity(@PathVariable String id, @RequestBody @Valid ActivityRequestDto activityRequestDto) {
        Optional<ActivityResponseDto> activity = crudActivity.update(id, activityRequestDto);

        return ResponseEntity.of(Optional.of(activity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteActivity(@PathVariable String id) {
        crudActivity.delete(id);

        return ResponseEntity.noContent().build();
    }
}

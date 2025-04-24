package bl.tech.realiza.gateways.controllers.impl.contracts.activity;

import bl.tech.realiza.gateways.controllers.interfaces.contracts.ActivityRepoController;
import bl.tech.realiza.gateways.requests.contracts.ActivityRepoRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ActivityRepoResponseDto;
import bl.tech.realiza.usecases.impl.contracts.CrudActivityRepoImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/contract/activity-repo")
@Tag(name = "Activity Repository")
public class ActivityRepoControllerImpl implements ActivityRepoController {
    private final CrudActivityRepoImpl crudActivityRepoImpl;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ActivityRepoResponseDto> createActivityRepo(@RequestBody ActivityRepoRequestDto activityRepoRequestDto) {
        return ResponseEntity.ok(crudActivityRepoImpl.save(activityRepoRequestDto));
    }

    @GetMapping("/{idActivityRepo}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ActivityRepoResponseDto>> getOneActivityRepo(@PathVariable String idActivityRepo) {
        return ResponseEntity.of(Optional.of(crudActivityRepoImpl.findOne(idActivityRepo)));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ActivityRepoResponseDto>> getAllActivitiesRepo(@RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "5") int size,
                                                                              @RequestParam(defaultValue = "idActivityRepo") String sort,
                                                                              @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        return ResponseEntity.of(Optional.of(crudActivityRepoImpl.findAll(pageable)));
    }

    @PutMapping("/{idActivityRepo}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ActivityRepoResponseDto>> updateActivityRepo(@PathVariable String idActivityRepo, @RequestBody ActivityRepoRequestDto activityRepoRequestDto) {
        return ResponseEntity.of(Optional.of(crudActivityRepoImpl.update(idActivityRepo, activityRepoRequestDto)));
    }

    @DeleteMapping("/{idActivityRepo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteActivityRepo(@PathVariable String idActivityRepo) {
        crudActivityRepoImpl.delete(idActivityRepo);
        return ResponseEntity.ok().build();
    }
}

package bl.tech.realiza.gateways.controllers.interfaces.contracts.activity;

import bl.tech.realiza.gateways.requests.contracts.activity.ActivityRepoRequestDto;
import bl.tech.realiza.gateways.responses.contracts.activity.ActivityRepoResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface ActivityRepoController {
    ResponseEntity<ActivityRepoResponseDto> createActivityRepo(ActivityRepoRequestDto activityRepoRequestDto);
    ResponseEntity<Optional<ActivityRepoResponseDto>> getOneActivityRepo(String id);
    ResponseEntity<Page<ActivityRepoResponseDto>> getAllActivitiesRepo(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<ActivityRepoResponseDto>> updateActivityRepo(String id, ActivityRepoRequestDto activityRepoRequestDto);
    ResponseEntity<Void> deleteActivityRepo(String id);
}

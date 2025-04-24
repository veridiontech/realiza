package bl.tech.realiza.gateways.controllers.interfaces.contracts;

import bl.tech.realiza.gateways.requests.contracts.ActivityRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ActivityDocumentResponseDto;
import bl.tech.realiza.gateways.responses.contracts.ActivityRepoResponseDto;
import bl.tech.realiza.gateways.responses.contracts.ActivityResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface ActivityControlller {
    ResponseEntity<ActivityResponseDto> createActivity(ActivityRequestDto activityRequestDto);
    ResponseEntity<Optional<ActivityResponseDto>> getOneActivity(String id);
    ResponseEntity<Page<ActivityResponseDto>> getAllActivities(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<List<ActivityResponseDto>> getAllActivitiesByBranch(String idBranch);
    ResponseEntity<List<ActivityDocumentResponseDto>> getAllDocumentsByActivity(String idActivity);
    ResponseEntity<ActivityDocumentResponseDto> addDocumentsToActivity(String idActivity, String idDocumentBranch);
    ResponseEntity<Optional<ActivityResponseDto>> updateActivity(String id, ActivityRequestDto activityRequestDto);
    ResponseEntity<Void> deleteActivity(String id);
}

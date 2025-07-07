package bl.tech.realiza.gateways.controllers.interfaces.contracts.activity;

import bl.tech.realiza.gateways.requests.contracts.activity.ActivityRequestDto;
import bl.tech.realiza.gateways.responses.contracts.activity.ActivityDocumentResponseDto;
import bl.tech.realiza.gateways.responses.contracts.activity.ActivityResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface ActivityControlller {
    ResponseEntity<ActivityResponseDto> createActivity(ActivityRequestDto activityRequestDto, Boolean replicate, List<String> branchIds);
    ResponseEntity<Optional<ActivityResponseDto>> getOneActivity(String id);
    ResponseEntity<Page<ActivityResponseDto>> getAllActivities(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<List<ActivityResponseDto>> getAllActivitiesByBranch(String idBranch);
    ResponseEntity<List<ActivityDocumentResponseDto>> getAllDocumentsByActivity(String idActivity);
    ResponseEntity<ActivityDocumentResponseDto> addDocumentsToActivity(String idActivity, String idDocumentBranch, Boolean replicate, List<String> branchIds);
    ResponseEntity<String> removeDocumentsFromActivity(String idActivity, String idDocumentBranch, Boolean replicate, List<String> branchIds);
    ResponseEntity<Optional<ActivityResponseDto>> updateActivity(String id, ActivityRequestDto activityRequestDto, Boolean replicate, List<String> branchIds);
    ResponseEntity<Void> deleteActivity(String id, Boolean replicate, List<String> branchIds);
}

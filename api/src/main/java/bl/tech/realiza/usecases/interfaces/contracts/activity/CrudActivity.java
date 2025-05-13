package bl.tech.realiza.usecases.interfaces.contracts.activity;

import bl.tech.realiza.gateways.requests.contracts.activity.ActivityRequestDto;
import bl.tech.realiza.gateways.responses.contracts.activity.ActivityDocumentResponseDto;
import bl.tech.realiza.gateways.responses.contracts.activity.ActivityResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Optional;

public interface CrudActivity {
    ActivityResponseDto save(ActivityRequestDto activityRequestDto);
    Optional<ActivityResponseDto> findOne(String id);
    Page<ActivityResponseDto> findAll(Pageable pageable);
    List<ActivityResponseDto> findAllByBranch(String idBranch);
    List<ActivityDocumentResponseDto> findAllDocumentsByActivity(String idActivity);
    ActivityDocumentResponseDto addDocumentToActivity(String idActivity, String idDocument);
    String removeDocumentFromActivity(String idActivity, String idDocumentBranch);
    Optional<ActivityResponseDto> update(String id, ActivityRequestDto activityRequestDto);
    void delete(String id);

    @Async
    void transferFromRepo(String idBranch);
}

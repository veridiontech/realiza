package bl.tech.realiza.usecases.interfaces.contracts;

import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.gateways.requests.contracts.ActivityRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ActivityDocumentResponseDto;
import bl.tech.realiza.gateways.responses.contracts.ActivityRepoResponseDto;
import bl.tech.realiza.gateways.responses.contracts.ActivityResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
}

package bl.tech.realiza.usecases.interfaces.contracts.activity;

import bl.tech.realiza.gateways.requests.contracts.activity.ActivityRequestDto;
import bl.tech.realiza.gateways.requests.contracts.activity.AddActivitiesToBranchesRequest;
import bl.tech.realiza.gateways.requests.contracts.activity.DocumentsToActivityRequest;
import bl.tech.realiza.gateways.responses.contracts.activity.ActivityDocumentResponseDto;
import bl.tech.realiza.gateways.responses.contracts.activity.ActivityResponseDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Optional;

public interface CrudActivity {
    ActivityResponseDto save(ActivityRequestDto activityRequestDto, Boolean replicate, List<String> branchIds);
    Optional<ActivityResponseDto> findOne(String id);
    Page<ActivityResponseDto> findAll(Pageable pageable);
    List<ActivityResponseDto> findAllByBranch(String idBranch);
    List<ActivityDocumentResponseDto> findAllDocumentsByActivity(String idActivity);
    ActivityDocumentResponseDto addDocumentToActivity(String idActivity, String idDocument, Boolean replicate, List<String> branchIds);
    String removeDocumentFromActivity(String idActivity, String idDocumentBranch, Boolean replicate, List<String> branchIds);
    Optional<ActivityResponseDto> update(String id, ActivityRequestDto activityRequestDto, Boolean replicate, List<String> branchIds);
    void delete(String id, Boolean replicate, List<String> branchIds);
    void transferFromRepo(String idBranch);
    void transferFromRepo(String branchId, List<String> activityIds);
    String addActivitiesToBranches(AddActivitiesToBranchesRequest request);
    List<ActivityDocumentResponseDto> addMultipleDocumentsToActivity(String idActivity, DocumentsToActivityRequest request);
}

package bl.tech.realiza.usecases.impl.contracts;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.activity.ActivityDocuments;
import bl.tech.realiza.domains.contract.activity.ActivityRepo;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityDocumentRepository;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityRepoRepository;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentBranchRepository;
import bl.tech.realiza.gateways.requests.contracts.ActivityRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ActivityDocumentResponseDto;
import bl.tech.realiza.gateways.responses.contracts.ActivityResponseDto;
import bl.tech.realiza.usecases.interfaces.contracts.CrudActivity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CrudActivityImpl implements CrudActivity {

    private final ActivityRepository activityRepository;
    private final BranchRepository branchRepository;
    private final ActivityRepoRepository activityRepoRepository;
    private final ActivityDocumentRepository activityDocumentRepository;
    private final DocumentBranchRepository documentBranchRepository;

    @Override
    public ActivityResponseDto save(ActivityRequestDto activityRequestDto) {
        Activity activity = Activity.builder()
                .title(activityRequestDto.getTitle())
                .risk(activityRequestDto.getRisk())
                .build();

        Activity savedActivity = activityRepository.save(activity);

        ActivityResponseDto activityResponse = ActivityResponseDto.builder()
                .idActivity(savedActivity.getIdActivity())
                .title(savedActivity.getTitle())
                .risk(savedActivity.getRisk())
                .build();

        return activityResponse;
    }

    @Override
    public Optional<ActivityResponseDto> findOne(String id) {
        Optional<Activity> activityOptional = activityRepository.findById(id);

        Activity activity = activityOptional.orElseThrow(() -> new NotFoundException("Activity not found"));

        ActivityResponseDto activityResponse = ActivityResponseDto.builder()
                .idActivity(activity.getIdActivity())
                .title(activity.getTitle())
                .risk(activity.getRisk())
                .build();

        return Optional.of(activityResponse);
    }

    @Override
    public Page<ActivityResponseDto> findAll(Pageable pageable) {
        Page<Activity> activityPage = activityRepository.findAll(pageable);

        Page<ActivityResponseDto> activities = activityPage.map(
                activity -> ActivityResponseDto.builder()
                        .idActivity(activity.getIdActivity())
                        .title(activity.getTitle())
                        .risk(activity.getRisk())
                        .build()
        );

        return activities;
    }

    @Override
    public List<ActivityResponseDto> findAllByBranch(String idBranch) {
        List<Activity> activities = activityRepository.findAllByBranch_IdBranch(idBranch);

        return activities.stream().map(
                        activity -> ActivityResponseDto.builder()
                                .idActivity(activity.getIdActivity())
                                .title(activity.getTitle())
                                .risk(activity.getRisk())
                                .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityDocumentResponseDto> findAllDocumentsByActivity(String idActivity) {
        List<ActivityDocuments> activityDocumentsList = activityDocumentRepository.findAllByActivity_IdActivity(idActivity);
        return activityDocumentsList.stream().map(
                activityDocuments -> ActivityDocumentResponseDto.builder()
                        .idAssociation(activityDocuments.getId())
                        .idActivity(activityDocuments.getActivity().getIdActivity())
                        .idDocument(activityDocuments.getDocumentBranch().getIdDocumentation())
                        .documentTitle(activityDocuments.getDocumentBranch().getTitle())
                        .build()
        ).toList();
    }

    @Override
    public ActivityDocumentResponseDto addDocumentToActivity(String idActivity, String idDocument) {
        Activity activity = activityRepository.findById(idActivity)
                .orElseThrow(() -> new NotFoundException("Activity not found"));

        DocumentBranch documentBranch = documentBranchRepository.findById(idDocument)
                .orElseThrow(() -> new NotFoundException("Documento not found"));

        ActivityDocuments activityDocuments = ActivityDocuments.builder()
                .activity(activity)
                .documentBranch(documentBranch)
                .build();
        ActivityDocuments savedActivityDocuments = activityDocumentRepository.save(activityDocuments);

        return ActivityDocumentResponseDto.builder()
                .idAssociation(savedActivityDocuments.getId())
                .idActivity(savedActivityDocuments.getActivity().getIdActivity())
                .idDocument(savedActivityDocuments.getDocumentBranch().getIdDocumentation())
                .documentTitle(savedActivityDocuments.getDocumentBranch().getTitle())
                .build();
    }

    @Override
    public String removeDocumentFromActivity(String idActivity, String idDocumentBranch) {
        ActivityDocuments savedActivityDocuments = activityDocumentRepository.findByActivity_IdActivityAndDocumentBranch_IdDocumentation(idActivity, idDocumentBranch);

        activityDocumentRepository.delete(savedActivityDocuments);

        return "Document removed successfully!";
    }

    @Override
    public Optional<ActivityResponseDto> update(String id, ActivityRequestDto activityRequestDto) {
        Optional<Activity> activityOptional = activityRepository.findById(id);

        Activity activity = activityOptional.orElseThrow(() -> new NotFoundException("Activity not found"));

        activity.setTitle(activityRequestDto.getTitle() != null ? activityRequestDto.getTitle() : activity.getTitle());
        activity.setRisk(activityRequestDto.getRisk() != null ? activityRequestDto.getRisk() : activity.getRisk());

        Activity savedActivity = activityRepository.save(activity);

        ActivityResponseDto activityResponse = ActivityResponseDto.builder()
                .idActivity(savedActivity.getIdActivity())
                .title(savedActivity.getTitle())
                .build();

        return Optional.of(activityResponse);
    }

    @Override
    public void delete(String id) {
        activityRepository.deleteById(id);
    }

    public void transferFromRepo(String idBranch) {
        Branch branch = branchRepository.findById(idBranch).orElseThrow(() -> new NotFoundException("Branch not found"));
        List<ActivityRepo> activityRepos = activityRepoRepository.findAll();
        List<Activity> activityList = activityRepos.stream().map(activityRepo -> Activity.builder()
                .title(activityRepo.getTitle())
                .risk(activityRepo.getRisk())
                .branch(branch)
                .build()).toList();

        activityRepository.saveAll(activityList);
    }
}

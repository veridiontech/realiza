package bl.tech.realiza.usecases.impl.contracts.activity;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.activity.ActivityDocuments;
import bl.tech.realiza.domains.contract.activity.ActivityDocumentsRepo;
import bl.tech.realiza.domains.contract.activity.ActivityRepo;
import bl.tech.realiza.domains.contract.serviceType.ServiceType;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.enums.RiskEnum;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityDocumentRepoRepository;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityDocumentRepository;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityRepoRepository;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentBranchRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.contracts.activity.ActivityRequestDto;
import bl.tech.realiza.gateways.responses.contracts.activity.ActivityDocumentResponseDto;
import bl.tech.realiza.gateways.responses.contracts.activity.ActivityResponseDto;
import bl.tech.realiza.gateways.responses.documents.DocumentForActivityResponseDto;
import bl.tech.realiza.services.queue.replication.ReplicationMessage;
import bl.tech.realiza.services.queue.replication.ReplicationQueueProducer;
import bl.tech.realiza.services.queue.setup.SetupMessage;
import bl.tech.realiza.services.auth.JwtService;
import bl.tech.realiza.services.queue.setup.SetupQueueProducer;
import bl.tech.realiza.usecases.interfaces.auditLogs.AuditLogService;
import bl.tech.realiza.usecases.interfaces.contracts.activity.CrudActivity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static bl.tech.realiza.domains.enums.AuditLogActionsEnum.*;
import static bl.tech.realiza.domains.enums.AuditLogTypeEnum.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrudActivityImpl implements CrudActivity {

    private final ActivityRepository activityRepository;
    private final BranchRepository branchRepository;
    private final ActivityRepoRepository activityRepoRepository;
    private final ActivityDocumentRepository activityDocumentRepository;
    private final DocumentBranchRepository documentBranchRepository;
    private final ActivityDocumentRepoRepository activityDocumentRepoRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogServiceImpl;
    private final SetupQueueProducer setupQueueProducer;
    private final ReplicationQueueProducer replicationQueueProducer;

    @Override
    public ActivityResponseDto save(ActivityRequestDto activityRequestDto, Boolean replicate, List<String> branchIds) {

        Activity activity = Activity.builder()
                .title(activityRequestDto.getTitle())
                .risk(activityRequestDto.getRisk())
                .branch(branchRepository.findById(activityRequestDto.getIdBranch())
                        .orElseThrow(() -> new NotFoundException("Branch not found")))
                .build();

        Activity savedActivity = activityRepository.save(activity);

        if (replicate == null) {
            replicate = false;
        }

        if (replicate) {
            replicationQueueProducer.send(new ReplicationMessage("CREATE_ACTIVITY",
                    branchIds,
                    savedActivity.getIdActivity(),
                    null,
                    null,
                    null,
                    RiskEnum.LOW,
                    RiskEnum.LOW));
        }

        if (JwtService.getAuthenticatedUserId() != null) {
            userRepository.findById(JwtService.getAuthenticatedUserId()).ifPresent(
                    userResponsible -> auditLogServiceImpl.createAuditLog(
                            savedActivity.getIdActivity(),
                            ACTIVITY,
                            userResponsible.getFullName() + " criou a atividade "
                                    + activity.getTitle(),
                            null,
                            CREATE,
                            userResponsible.getIdUser()));
        }

        return ActivityResponseDto.builder()
                .idActivity(savedActivity.getIdActivity())
                .title(savedActivity.getTitle())
                .risk(savedActivity.getRisk())
                .idBranch(savedActivity.getBranch().getIdBranch())
                .build();
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

        return activityPage.map(
                activity -> ActivityResponseDto.builder()
                        .idActivity(activity.getIdActivity())
                        .title(activity.getTitle())
                        .risk(activity.getRisk())
                        .build()
        );
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
                .sorted(Comparator.comparing(ActivityResponseDto::getTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }

    @Override
    public List<ActivityDocumentResponseDto> findAllDocumentsByActivity(String idActivity) {
        String branchId = activityRepository.findBranchIdByActivity(idActivity);

        if (branchId == null) {
            throw new NotFoundException("Activity not found");
        }

        List<ActivityDocuments> activityDocumentsList = activityDocumentRepository.findAllByActivity_IdActivity(idActivity);
        List<DocumentForActivityResponseDto> documentBranches = documentBranchRepository.findAllByBranchForActivity(branchId);

        Set<String> documentBranchIds = activityDocumentsList.stream()
                .map(doc -> doc.getDocumentBranch().getIdDocumentation())
                .collect(Collectors.toSet());

        return documentBranches.stream()
                .filter(documentBranch -> {
                    String type = documentBranch.getType();
                    return type != null && (
                            type.equalsIgnoreCase("saude") ||
                                    type.equalsIgnoreCase("segurança do trabalho") ||
                                    type.equalsIgnoreCase("meio ambiente")
                    );
                })
                .map(documentBranch -> {
                    boolean isSelected = documentBranchIds.contains(documentBranch.getIdDocumentation());

                    ActivityDocuments associatedActivityDocument = activityDocumentsList.stream()
                            .filter(documents -> documents.getDocumentBranch().getIdDocumentation().equals(documentBranch.getIdDocumentation()))
                            .findFirst()
                            .orElse(null);

                    return ActivityDocumentResponseDto.builder()
                            .idAssociation(associatedActivityDocument != null ? associatedActivityDocument.getId() : null)
                            .idDocument(documentBranch.getIdDocumentation())
                            .documentTitle(documentBranch.getTitle())
                            .idActivity(idActivity)
                            .selected(isSelected)
                            .build();
                })
                .sorted(Comparator.comparing(ActivityDocumentResponseDto::getDocumentTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }

    @Override
    public ActivityDocumentResponseDto addDocumentToActivity(String idActivity, String idDocument, Boolean replicate, List<String> branchIds) {
        Activity activity = activityRepository.findById(idActivity)
                .orElseThrow(() -> new NotFoundException("Activity not found"));

        DocumentBranch documentBranch = documentBranchRepository.findById(idDocument)
                .orElseThrow(() -> new NotFoundException("Documento not found"));

        ActivityDocuments activityDocuments = ActivityDocuments.builder()
                .activity(activity)
                .documentBranch(documentBranch)
                .build();
        ActivityDocuments savedActivityDocuments = activityDocumentRepository.save(activityDocuments);

        if (replicate == null) {
            replicate = false;
        }

        if (replicate) {
            replicationQueueProducer.send(new ReplicationMessage("ALLOCATE_DOCUMENT_FROM_ACTIVITY",
                    branchIds,
                    null,
                    savedActivityDocuments.getActivity().getIdActivity(),
                    null,
                    savedActivityDocuments.getDocumentBranch().getIdDocumentation(),
                    RiskEnum.LOW,
                    RiskEnum.LOW));
        }

        if (JwtService.getAuthenticatedUserId() != null) {
            userRepository.findById(JwtService.getAuthenticatedUserId()).ifPresent(
                    userResponsible -> auditLogServiceImpl.createAuditLog(
                            activity.getIdActivity(),
                            ACTIVITY,
                            userResponsible.getFullName() + " atribuiu o documento "
                                    + documentBranch.getTitle() + " a atividade "
                                    + activity.getTitle(),
                            null,
                            ALLOCATE,
                            userResponsible.getIdUser()));
        }

        return ActivityDocumentResponseDto.builder()
                .idAssociation(savedActivityDocuments.getId())
                .idActivity(savedActivityDocuments.getActivity().getIdActivity())
                .idDocument(savedActivityDocuments.getDocumentBranch().getIdDocumentation())
                .documentTitle(savedActivityDocuments.getDocumentBranch().getTitle())
                .build();
    }

    @Override
    public String removeDocumentFromActivity(String idActivity, String idDocumentBranch, Boolean replicate, List<String> branchIds) {
        ActivityDocuments savedActivityDocuments = activityDocumentRepository.findByActivity_IdActivityAndDocumentBranch_IdDocumentation(idActivity, idDocumentBranch);

        if (replicate == null) {
            replicate = false;
        }

        if (replicate) {
            replicationQueueProducer.send(new ReplicationMessage("DEALLOCATE_DOCUMENT_FROM_ACTIVITY",
                    branchIds,
                    null,
                    savedActivityDocuments.getActivity().getIdActivity(),
                    null,
                    savedActivityDocuments.getDocumentBranch().getIdDocumentation(),
                    RiskEnum.LOW,
                    RiskEnum.LOW));
        }

        if (JwtService.getAuthenticatedUserId() != null) {
            userRepository.findById(JwtService.getAuthenticatedUserId()).ifPresent(
                    userResponsible -> auditLogServiceImpl.createAuditLog(
                            savedActivityDocuments.getActivity().getIdActivity(),
                            ACTIVITY,
                            userResponsible.getFullName() + " removeu o documento "
                                    + savedActivityDocuments.getDocumentBranch().getTitle()
                                    + " da atividade "
                                    + savedActivityDocuments.getActivity().getTitle(),
                            null,
                            DEALLOCATE,
                            userResponsible.getIdUser()));
        }

        activityDocumentRepository.delete(savedActivityDocuments);

        return "Document removed successfully!";
    }

    @Override
    public Optional<ActivityResponseDto> update(String id, ActivityRequestDto activityRequestDto, Boolean replicate, List<String> branchIds) {
        Optional<Activity> activityOptional = activityRepository.findById(id);

        Activity activity = activityOptional.orElseThrow(() -> new NotFoundException("Activity not found"));
        String oldTitle = activity.getTitle();
        RiskEnum oldRisk = activity.getRisk();

        activity.setTitle(activityRequestDto.getTitle() != null
                ? activityRequestDto.getTitle()
                : activity.getTitle());
        activity.setRisk(activityRequestDto.getRisk() != null
                ? activityRequestDto.getRisk()
                : activity.getRisk());

        Activity savedActivity = activityRepository.save(activity);

        if (replicate == null) {
            replicate = false;
        }

        if (replicate) {
            replicationQueueProducer.send(new ReplicationMessage("UPDATE_ACTIVITY",
                    branchIds,
                    savedActivity.getIdActivity(),
                    null,
                    null,
                    oldTitle,
                    oldRisk,
                    RiskEnum.LOW));
        }

        ActivityResponseDto activityResponse = ActivityResponseDto.builder()
                .idActivity(savedActivity.getIdActivity())
                .title(savedActivity.getTitle())
                .build();

        if (JwtService.getAuthenticatedUserId() != null) {
            userRepository.findById(JwtService.getAuthenticatedUserId()).ifPresent(
                    userResponsible -> auditLogServiceImpl.createAuditLog(
                            savedActivity.getIdActivity(),
                            ACTIVITY,
                            userResponsible.getFullName() + " atualizou a atividade "
                                    + activity.getTitle(),
                            null,
                            UPDATE,
                            userResponsible.getIdUser()));
        }

        return Optional.of(activityResponse);
    }

    @Override
    public void delete(String id, Boolean replicate, List<String> branchIds) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Activity not found"));

        if (replicate == null) {
            replicate = false;
        }

        if (replicate) {
            replicationQueueProducer.send(new ReplicationMessage("DELETE_ACTIVITY",
                    branchIds,
                    null,
                    null,
                    null,
                    activity.getTitle(),
                    RiskEnum.LOW,
                    RiskEnum.LOW));
        }

        if (JwtService.getAuthenticatedUserId() != null) {
            userRepository.findById(JwtService.getAuthenticatedUserId()).ifPresent(
                    userResponsible -> auditLogServiceImpl.createAuditLog(
                            activity.getIdActivity(),
                            ACTIVITY,
                            userResponsible.getFullName() + " deletou a atividade "
                                    + activity.getTitle(),
                            null,
                            DELETE,
                            userResponsible.getIdUser()));
        }
        activityRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void transferFromRepo(String idBranch) {
        Branch branch = branchRepository.findById(idBranch)
                .orElseThrow(() -> new NotFoundException("Branch not found"));

        List<ActivityRepo> activityRepos = activityRepoRepository.findAll();
        List<Activity> newActivities = new ArrayList<>();
        Map<String, Activity> repoToNewActivityMap = new HashMap<>();

        for (ActivityRepo repo : activityRepos) {
            Activity newActivity = Activity.builder()
                    .title(repo.getTitle())
                    .risk(repo.getRisk())
                    .branch(branch)
                    .build();
            newActivities.add(newActivity);
            repoToNewActivityMap.put(repo.getIdActivity(), newActivity);

            if (newActivities.size() == 50) {
                activityRepository.saveAll(newActivities);
                newActivities.clear();
            }
        }

        if (!newActivities.isEmpty()) {
            activityRepository.saveAll(newActivities);
        }

        List<DocumentBranch> allBranchDocs = documentBranchRepository.findAllByBranch_IdBranch(branch.getIdBranch());
        Map<String, DocumentBranch> matrixIdToBranchDocMap = allBranchDocs.stream()
                .filter(doc -> doc.getDocumentMatrix() != null)
                .collect(Collectors.toMap(doc -> doc.getDocumentMatrix().getIdDocument(), doc -> doc));

        List<ActivityDocumentsRepo> docsRepo = activityDocumentRepoRepository.findAll();

        List<ActivityDocuments> newActivityDocs = new ArrayList<>();

        for (ActivityDocumentsRepo docRepo : docsRepo) {
            Activity newActivity = repoToNewActivityMap.get(docRepo.getActivity().getIdActivity());

            DocumentMatrix matrix = docRepo.getDocumentMatrix();
            if (newActivity == null || matrix == null) continue;

            DocumentBranch branchDoc = matrixIdToBranchDocMap.get(matrix.getIdDocument());
            if (branchDoc == null) continue;

            newActivityDocs.add(ActivityDocuments.builder()
                    .activity(newActivity)
                    .documentBranch(branchDoc)
                    .isSelected(true)
                    .build());

            if (newActivityDocs.size() == 50) {
                activityDocumentRepository.saveAll(newActivityDocs);
                newActivityDocs.clear();
            }
        }

        if (!newActivityDocs.isEmpty()) {
            activityDocumentRepository.saveAll(newActivityDocs);
        }
    }
}

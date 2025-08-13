package bl.tech.realiza.services.queue.replication;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.activity.ActivityDocuments;
import bl.tech.realiza.domains.contract.serviceType.ServiceTypeBranch;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.enums.RiskEnum;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityDocumentRepository;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityRepository;
import bl.tech.realiza.gateways.repositories.contracts.serviceType.ServiceTypeBranchRepository;
import bl.tech.realiza.gateways.repositories.documents.DocumentRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentBranchRepository;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReplicationService {

    private final BranchRepository branchRepository;
    private final DocumentMatrixRepository documentMatrixRepository;
    private final DocumentBranchRepository documentBranchRepository;
    private final ActivityRepository activityRepository;
    private final ActivityDocumentRepository activityDocumentRepository;
    private final ServiceTypeBranchRepository serviceTypeBranchRepository;
    private final DocumentRepository documentRepository;

    public void replicateCreateActivity(String activityId, List<String> branchIds) {
        Activity activityBase = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Activity not found"));

        branchIds.remove(activityBase.getBranch().getIdBranch());
        List<Branch> branches = branchRepository.findAllById(branchIds);

        branches.remove(activityBase.getBranch());

        List<Activity> batch = new ArrayList<>(50);
        for (Branch branch : branches) {
            batch.add(
                    Activity.builder()
                            .title(activityBase.getTitle())
                            .risk(activityBase.getRisk())
                            .branch(branch)
                            .activityRepo(activityBase.getActivityRepo())
                            .build()
            );

            if (batch.size() == 50) {
                activityRepository.saveAll(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            activityRepository.saveAll(batch);
        }
    }

    public void replicateUpdateActivity(String activityId, String title, RiskEnum risk, List<String> branchIds) {
        Activity activityBase = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Activity not found"));

        branchIds.remove(activityBase.getBranch().getIdBranch());

        List<Activity> activities = new ArrayList<>();

        for (String branch : branchIds) {
            activities.addAll(activityRepository.findAllByBranch_IdBranchAndTitle(branch, activityBase.getTitle()));
        }

        activities.remove(activityBase);

        List<Activity> batch = new ArrayList<>(50);
        for (Activity activity : activities) {

            activity.setTitle(title);
            activity.setRisk(risk);

            batch.add(activity);

            if (batch.size() == 50) {
                activityRepository.saveAll(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            activityRepository.saveAll(batch);
        }
    }

    public void replicateDeleteActivity(String title, List<String> branchIds) {

        List<Activity> activities = new ArrayList<>();

        for (String branch : branchIds) {
            activities.addAll(activityRepository.findAllByBranch_IdBranchAndTitle(branch, title));
        }

        List<Activity> batch = new ArrayList<>(50);
        for (Activity activity : activities) {

            batch.add(activity);

            if (batch.size() == 50) {
                activityRepository.deleteAll(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            activityRepository.deleteAll(batch);
        }
    }

    public void replicateCreateServiceType(String serviceTypeBranchId, List<String> branchIds) {
        ServiceTypeBranch serviceTypeBranch = serviceTypeBranchRepository.findById(serviceTypeBranchId)
                .orElseThrow(() -> new NotFoundException("Activity not found"));

        branchIds.remove(serviceTypeBranch.getBranch().getIdBranch());
        List<Branch> branches = branchRepository.findAllById(branchIds);

        branches.remove(serviceTypeBranch.getBranch());

        List<ServiceTypeBranch> batch = new ArrayList<>(50);
        for (Branch branch : branches) {
            batch.add(
                    ServiceTypeBranch.builder()
                            .title(serviceTypeBranch.getTitle())
                            .risk(serviceTypeBranch.getRisk())
                            .branch(branch)
                            .build()
            );

            if (batch.size() == 50) {
                serviceTypeBranchRepository.saveAll(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            serviceTypeBranchRepository.saveAll(batch);
        }
    }

    public void replicateUpdateServiceType(String serviceTypeBranchId, String title, RiskEnum risk, List<String> branchIds) {
        ServiceTypeBranch activityBase = serviceTypeBranchRepository.findById(serviceTypeBranchId)
                .orElseThrow(() -> new NotFoundException("Activity not found"));

        branchIds.remove(activityBase.getBranch().getIdBranch());

        List<ServiceTypeBranch> activities = new ArrayList<>();
        for (String branchId : branchIds) {
            activities.addAll(serviceTypeBranchRepository.findAllByBranch_IdBranchAndTitleAndRisk(branchId, title,risk));
        }

        activities.remove(activityBase);

        List<ServiceTypeBranch> batch = new ArrayList<>(50);
        for (ServiceTypeBranch serviceTypeBranch : activities) {

            serviceTypeBranch.setTitle(title);
            serviceTypeBranch.setRisk(risk);

            batch.add(serviceTypeBranch);

            if (batch.size() == 50) {
                serviceTypeBranchRepository.saveAll(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            serviceTypeBranchRepository.saveAll(batch);
        }
    }

    public void replicateDeleteServiceType(String title, RiskEnum risk, List<String> branchIds) {
        List<ServiceTypeBranch> serviceTypeBranches = new ArrayList<>();

        for (String branchId : branchIds) {
            serviceTypeBranches.addAll(serviceTypeBranchRepository.findAllByBranch_IdBranchAndTitleAndRisk(branchId, title, risk));
        }

        List<ServiceTypeBranch> batch = new ArrayList<>(50);
        for (ServiceTypeBranch serviceTypeBranch : serviceTypeBranches) {

            batch.add(serviceTypeBranch);

            if (batch.size() == 50) {
                serviceTypeBranchRepository.deleteAll(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            serviceTypeBranchRepository.deleteAll(batch);
        }
    }

    public void replicateAllocateDocumentToActivity(String documentId, String activityId, List<String> branchIds) {
        Activity activityBase = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Activity not found"));

        DocumentBranch document = documentBranchRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Document not found"));

        branchIds.remove(activityBase.getBranch().getIdBranch());

        List<Activity> activities = new ArrayList<>();
        List<DocumentBranch> documents = new ArrayList<>();
        for (String branchId : branchIds) {
            activities.addAll(activityRepository.findAllByBranch_IdBranchAndTitleAndRisk(branchId, activityBase.getTitle(), activityBase.getRisk()));
            documents.addAll(documentBranchRepository.findAllByBranch_IdBranchAndTitle(branchId, document.getTitle()));
        }
        activities.remove(activityBase);
        documents.remove(document);

        Map<String, Activity> activityBranchMap = activities.stream()
                .collect(Collectors.toMap(activity -> activity.getBranch().getIdBranch(), activity -> activity));

        List<ActivityDocuments> batch = new ArrayList<>(50);
        for (DocumentBranch doc : documents) {
            if (activityBranchMap.containsKey(doc.getBranch().getIdBranch())) {
                Activity activity = activityBranchMap.get(doc.getBranch().getIdBranch());
                if (activityDocumentRepository.findByActivity_IdActivityAndDocumentBranch_IdDocumentation(activity.getIdActivity(),doc.getIdDocumentation()) == null) {
                    batch.add(
                            ActivityDocuments.builder()
                                    .activity(activity)
                                    .documentBranch(document)
                                    .build()
                    );
                }

                if (batch.size() == 50) {
                    activityDocumentRepository.saveAll(batch);
                    batch.clear();
                }
            }
        }
        if (!batch.isEmpty()) {
            activityDocumentRepository.saveAll(batch);
        }
    }

    public void replicateDeallocateDocumentToActivity(String documentId, String activityId, List<String> branchIds) {
        Activity activityBase = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Activity not found"));

        DocumentBranch document = documentBranchRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Document not found"));

        List<ActivityDocuments> activityDocumentsList = new ArrayList<>();
        branchIds.remove(activityBase.getBranch().getIdBranch());
        for (String branchId : branchIds) {
            activityDocumentsList.addAll(activityDocumentRepository
                    .findAllByActivity_Branch_IdBranchAndActivity_TitleAndDocumentBranch_Branch_IdBranchAndDocumentBranch_Title(
                            branchId,
                            activityBase.getTitle(),
                            branchId,
                            document.getTitle()));
        }

        List<ActivityDocuments> batch = new ArrayList<>(50);
        for (ActivityDocuments activityDocument : activityDocumentsList) {
            batch.add(activityDocument);

            if (batch.size() == 50) {
                activityDocumentRepository.deleteAll(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            activityDocumentRepository.deleteAll(batch);
        }
    }

    public void replicateAllocateDocumentToBranch(String documentId, String title, List<String> branchIds) {
        DocumentBranch documentBase = documentBranchRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Document not found"));

        List<DocumentBranch> documentBranchList = new ArrayList<>();
        branchIds.remove(documentBase.getBranch().getIdBranch());
        for (String branchId : branchIds) {
            documentBranchList.addAll(documentBranchRepository.findAllByBranch_IdBranchAndTitle(branchId, title));
        }

        documentBranchList.remove(documentBase);

        List<DocumentBranch> batch = new ArrayList<>(50);
        for (DocumentBranch document : documentBranchList) {
            document.setIsActive(true);
            batch.add(document);

            if (batch.size() == 50) {
                documentBranchRepository.saveAll(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            documentBranchRepository.saveAll(batch);
        }
    }

    public void replicateDeallocateDocumentToBranch(String documentId, String title, List<String> branchIds) {
        DocumentBranch documentBase = documentBranchRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Document not found"));

        List<DocumentBranch> documentBranchList = new ArrayList<>();
        branchIds.remove(documentBase.getBranch().getIdBranch());
        for (String branchId : branchIds) {
            documentBranchList.addAll(documentBranchRepository.findAllByBranch_IdBranchAndTitle(branchId, title));
        }

        documentBranchList.remove(documentBase);

        List<DocumentBranch> batch = new ArrayList<>(50);
        for (DocumentBranch document : documentBranchList) {
            document.setIsActive(false);
            batch.add(document);

            if (batch.size() == 50) {
                documentBranchRepository.saveAll(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            documentBranchRepository.saveAll(batch);
        }
    }

    public void replicateExpirationDateDocumentUpdate(String documentId, List<String> branchIds) {
        DocumentBranch documentBase = documentBranchRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Document not found"));

        List<DocumentBranch> documentBranchList = new ArrayList<>();
        branchIds.remove(documentBase.getBranch().getIdBranch());
        for (String branchId : branchIds) {
            documentBranchList.addAll(documentBranchRepository.findAllByBranch_IdBranchAndTitle(branchId, documentBase.getTitle()));
        }

        documentBranchList.remove(documentBase);

        List<DocumentBranch> batch = new ArrayList<>(50);
        for (DocumentBranch document : documentBranchList) {
            document.setExpirationDateAmount(documentBase.getExpirationDateAmount());
            document.setExpirationDateUnit(documentBase.getExpirationDateUnit());
            batch.add(document);

            if (batch.size() == 50) {
                documentBranchRepository.saveAll(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            documentBranchRepository.saveAll(batch);
        }
    }

    public void setupReplicateDocumentMatrixFromSystem(String documentId) {
        DocumentMatrix documentMatrix = documentMatrixRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Document Matrix not found"));

        List<Document> documentBranchList = documentRepository.findAllByDocumentMatrix_IdDocument(documentMatrix.getIdDocument());

        List<Document> batch = new ArrayList<>(50);
        for (Document document : documentBranchList) {
            document.setExpirationDateAmount(documentMatrix.getExpirationDateAmount());
            document.setExpirationDateUnit(documentMatrix.getExpirationDateUnit());
            batch.add(document);

            if (batch.size() == 50) {
                documentRepository.saveAll(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            documentRepository.saveAll(batch);
        }
    }

    public void setupCreateDocumentMatrixReplicateForBranches(String documentId) {
        DocumentMatrix documentMatrix = documentMatrixRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Document not found"));

        List<Branch> branches = branchRepository.findAll();
        List<DocumentBranch> batch = new ArrayList<>(50);
        for (Branch branch : branches) {
            batch.add(DocumentBranch.builder()
                    .title(documentMatrix.getName())
                    .type(documentMatrix.getType())
                    .status(Document.Status.PENDENTE)
                    .isActive(false)
                    .branch(branch)
                    .documentMatrix(documentMatrix)
                    .validity(documentMatrix.getValidity())
                    .expirationDateAmount(documentMatrix.getExpirationDateAmount())
                    .expirationDateUnit(documentMatrix.getExpirationDateUnit())
                    .build());

            if (batch.size() == 50) {
                documentBranchRepository.saveAll(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            documentBranchRepository.saveAll(batch);
        }
    }
}

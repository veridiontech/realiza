package bl.tech.realiza.services.setup;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.activity.ActivityDocuments;
import bl.tech.realiza.domains.contract.serviceType.ServiceType;
import bl.tech.realiza.domains.contract.serviceType.ServiceTypeBranch;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.user.profile.Profile;
import bl.tech.realiza.domains.user.profile.ProfileRepo;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityDocumentRepository;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityRepository;
import bl.tech.realiza.gateways.repositories.contracts.serviceType.ServiceTypeBranchRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentBranchRepository;
import bl.tech.realiza.gateways.repositories.documents.employee.DocumentEmployeeRepository;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.repositories.users.profile.ProfileRepoRepository;
import bl.tech.realiza.gateways.repositories.users.profile.ProfileRepository;
import bl.tech.realiza.usecases.interfaces.contracts.CrudServiceType;
import bl.tech.realiza.usecases.interfaces.contracts.activity.CrudActivity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SetupService {

    private final BranchRepository branchRepository;
    private final DocumentMatrixRepository documentMatrixRepository;
    private final DocumentBranchRepository documentBranchRepository;
    private final ActivityRepository activityRepository;
    private final ActivityDocumentRepository activityDocumentRepository;
    private final DocumentProviderSupplierRepository documentProviderSupplierRepository;
    private final DocumentProviderSubcontractorRepository documentProviderSubcontractorRepository;
    private final EmployeeRepository employeeRepository;
    private final DocumentEmployeeRepository documentEmployeeRepository;
    private final ClientRepository clientRepository;
    private final ContractProviderSupplierRepository contractProviderSupplierRepository;
    private final ContractProviderSubcontractorRepository contractProviderSubcontractorRepository;
    private final ContractRepository contractRepository;
    private final ServiceTypeBranchRepository serviceTypeBranchRepository;
    private final ProfileRepoRepository profileRepoRepository;
    private final ProfileRepository profileRepository;
    private final CrudServiceType crudServiceType;
    private final CrudActivity crudActivity;

    public void setupNewClient(String clientId) {
        log.info("Started setup client ⌛ {}", clientId);
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found"));
        log.info("Finished setup client ✔️ {}", clientId);
        crudServiceType.transferFromRepoToClient(client.getIdClient());
    }

    public void setupNewClientProfiles(String clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found"));
        List<ProfileRepo> profileRepos = profileRepoRepository.findAll();
        List<Profile> profiles = new ArrayList<>();
        for (ProfileRepo profileRepo : profileRepos) {
            profiles.add(
                    Profile.builder()
                            .name(profileRepo.getName())
                            .description(profileRepo.getDescription())
                            .admin(profileRepo.getAdmin())
                            .viewer(profileRepo.getViewer())
                            .manager(profileRepo.getManager())
                            .inspector(profileRepo.getInspector())
                            .documentViewer(profileRepo.getDocumentViewer())
                            .registrationUser(profileRepo.getRegistrationUser())
                            .registrationContract(profileRepo.getRegistrationContract())
                            .laboral(profileRepo.getLaboral())
                            .workplaceSafety(profileRepo.getWorkplaceSafety())
                            .registrationAndCertificates(profileRepo.getRegistrationAndCertificates())
                            .general(profileRepo.getGeneral())
                            .health(profileRepo.getHealth())
                            .environment(profileRepo.getEnvironment())
                            .concierge(profileRepo.getConcierge())
                            .client(client)
                            .build()
            );

            if (profiles.size() == 50) {
                profileRepository.saveAll(profiles);
                profiles.clear();
            }
        }

        if (!profiles.isEmpty()) {
            profileRepository.saveAll(profiles);
        }
    }

    public void setupBranch(String branchId) {
        log.info("Started setup branch ⌛ {}", branchId);
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new NotFoundException("Branch not found"));
        crudServiceType.transferFromClientToBranch(branch.getClient().getIdClient(), branch.getIdBranch());

        List<DocumentBranch> batch = new ArrayList<>(50);
        for (var documentMatrix : documentMatrixRepository.findAll()) {
            batch.add(DocumentBranch.builder()
                    .title(documentMatrix.getName())
                    .type(documentMatrix.getType())
                    .status(Document.Status.PENDENTE)
                    .isActive(true)
                    .branch(branch)
                    .documentMatrix(documentMatrix)
                    .build());

            if (batch.size() == 50) {
                documentBranchRepository.saveAll(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            documentBranchRepository.saveAll(batch);
        }
        crudActivity.transferFromRepo(branch.getIdBranch());

        log.info("Finished setup branch ✔️ {}", branchId);
    }

    public void setupContractSupplier(String contractProviderSupplierId, List<String> activityIds) {
        log.info("Started setup contract supplier ⌛ {}", contractProviderSupplierId);
        List<Activity> activities = new ArrayList<>(List.of());
        List<String> idDocuments = new ArrayList<>(List.of());
        List<DocumentBranch> documentBranch;

        ContractProviderSupplier contractProviderSupplier = contractProviderSupplierRepository.findById(contractProviderSupplierId)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        if (contractProviderSupplier.getHse() && !activityIds.isEmpty()) {
            activities = activityRepository.findAllById(activityIds);
            if (activities.isEmpty()) {
                throw new NotFoundException("Activities not found");
            }

            activities.forEach(
                    activity -> {
                        List<ActivityDocuments> activityDocumentsList = activityDocumentRepository.findAllByActivity_IdActivity(activity.getIdActivity());
                        activityDocumentsList.forEach(
                                activityDocument -> idDocuments.add(activityDocument.getDocumentBranch().getIdDocumentation())
                        );
                    }
            );
        }

        contractProviderSupplier.setActivities(!activities.isEmpty()
                ? activities
                : contractProviderSupplier.getActivities());

        contractProviderSupplierRepository.save(contractProviderSupplier);

        documentBranch = documentBranchRepository.findAllById(idDocuments);

        ProviderSupplier finalNewProviderSupplier = contractProviderSupplier.getProviderSupplier();
        List<Contract> contracts = new ArrayList<>();
        contracts.add(contractProviderSupplier);

        List<DocumentProviderSupplier> batch = new ArrayList<>(50);
        for (DocumentBranch document : documentBranch) {
            batch.add(DocumentProviderSupplier.builder()
                    .title(document.getTitle())
                    .status(Document.Status.PENDENTE)
                    .type(document.getType())
                    .isActive(true)
                    .documentMatrix(document.getDocumentMatrix())
                    .providerSupplier(finalNewProviderSupplier)
                    .contracts(contracts)
                    .build());

            if (batch.size() == 50) {
                documentProviderSupplierRepository.saveAll(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            documentProviderSupplierRepository.saveAll(batch);
        }
        log.info("Finished setup contract supplier ✔️ {}", contractProviderSupplierId);
    }

    public void setupContractSubcontractor(String contractProviderSubcontractorId, List<String> activityIds) {
        log.info("Started setup contract subcontractor ⌛ {}", contractProviderSubcontractorId);
        List<Activity> activities = new ArrayList<>(List.of());;
        List<DocumentProviderSupplier> documentSupplier;
        List<String> idDocuments = new ArrayList<>(List.of());

        ContractProviderSubcontractor contractProviderSubcontractor = contractProviderSubcontractorRepository.findById(contractProviderSubcontractorId)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        if (contractProviderSubcontractor.getHse() && !activityIds.isEmpty()) {
            activities = activityRepository.findAllById(activityIds);
            if (activities.isEmpty()) {
                throw new NotFoundException("Activities not found");
            }

            activities.forEach(
                    activity -> {
                        List<ActivityDocuments> activityDocumentsList = activityDocumentRepository.findAllByActivity_IdActivity(activity.getIdActivity());
                        activityDocumentsList.forEach(
                                activityDocument -> idDocuments.add(activityDocument.getDocumentBranch().getIdDocumentation())
                        );
                    }
            );
        }

        contractProviderSubcontractor.setActivities(!activities.isEmpty()
                ? activities
                : contractProviderSubcontractor.getActivities());

        contractProviderSubcontractorRepository.save(contractProviderSubcontractor);

        documentSupplier = documentProviderSupplierRepository.findAllById(idDocuments);

        ProviderSubcontractor finalNewProviderSubcontractor = contractProviderSubcontractor.getProviderSubcontractor();
        List<DocumentProviderSubcontractor> batch = new ArrayList<>(50);

        List<Contract> contracts = new ArrayList<>();
        contracts.add(contractProviderSubcontractor);

        for (DocumentProviderSupplier document : documentSupplier) {
            batch.add(DocumentProviderSubcontractor.builder()
                    .title(document.getTitle())
                    .status(Document.Status.PENDENTE)
                    .type(document.getType())
                    .isActive(true)
                    .documentMatrix(document.getDocumentMatrix())
                    .providerSubcontractor(finalNewProviderSubcontractor)
                    .contracts(contracts)
                    .build());

            if (batch.size() == 50) {
                documentProviderSubcontractorRepository.saveAll(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            documentProviderSubcontractorRepository.saveAll(batch);
        }
        log.info("Finished setup contract subcontractor ✔️ {}", contractProviderSubcontractorId);
    }

    public void setupEmployeeToContractSupplier(String contractProviderSupplierId, List<String> employeeIds) {
        log.info("Started setup employee to contract supplier ⌛ {}, {}", employeeIds, contractProviderSupplierId);

        ContractProviderSupplier contractProviderSupplier = contractProviderSupplierRepository.findById(contractProviderSupplierId)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        List<Employee> employees = employeeRepository.findAllById(employeeIds);
        ProviderSupplier providerSupplier = contractProviderSupplier.getProviderSupplier();

        List<DocumentProviderSupplier> documentSupplier = new ArrayList<>();
        documentSupplier.addAll(documentProviderSupplierRepository
                .findAllByProviderSupplier_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(
                        providerSupplier.getIdProvider(), "Documento pessoa", true));
        documentSupplier.addAll(documentProviderSupplierRepository
                .findAllByProviderSupplier_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(
                        providerSupplier.getIdProvider(), "Treinamentos e certificações", true));

        List<DocumentEmployee> batch = new ArrayList<>(50);
        List<Contract> contracts = new ArrayList<>();
        contracts.add(contractProviderSupplier);

        for (Employee employee : employees) {
            Boolean existingDocumentCheck = false;
            List<DocumentEmployee> documentEmployeeList = documentEmployeeRepository.findAllByEmployee_IdEmployee(employee.getIdEmployee());

            for (DocumentProviderSupplier document : documentSupplier) {
                DocumentEmployee existingDocument = documentEmployeeList.stream()
                        .filter(de -> de.getTitle().equals(document.getTitle()))
                        .findFirst()
                        .orElse(null);
                if (existingDocument != null && document.getDocumentMatrix().getIsDocumentUnique()) {
                    existingDocument.getContracts().add(contractProviderSupplier);
                    existingDocumentCheck = true;
                } else if (existingDocument == null) {
                    batch.add(DocumentEmployee.builder()
                            .title(document.getTitle())
                            .status(Document.Status.PENDENTE)
                            .type(document.getType())
                            .isActive(true)
                            .documentMatrix(document.getDocumentMatrix())
                            .employee(employee)
                            .contracts(contracts)
                            .build());
                }
                if (batch.size() == 50) {
                    documentEmployeeRepository.saveAll(batch);
                    batch.clear();
                }
            }
            if (existingDocumentCheck) {
                documentEmployeeRepository.saveAll(documentEmployeeList);
            }
        }

        if (!batch.isEmpty()) {
            documentEmployeeRepository.saveAll(batch);
        }
        log.info("Finished setup employee to contract supplier ✔️ {}, {}", employeeIds, contractProviderSupplierId);
    }

    public void setupEmployeeToContractSubcontract(String contractProviderSubcontractorId, List<String> employeeIds) {
        log.info("Started setup employee to contract subcontractor ⌛ {}, {}", employeeIds, contractProviderSubcontractorId);

        ContractProviderSubcontractor contractProviderSubcontractor = contractProviderSubcontractorRepository.findById(contractProviderSubcontractorId)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        List<Employee> employees = employeeRepository.findAllById(employeeIds);
        ProviderSubcontractor providerSubcontractor = contractProviderSubcontractor.getProviderSubcontractor();

        List<DocumentProviderSubcontractor> documentSubcontractor = new ArrayList<>();
        documentSubcontractor.addAll(documentProviderSubcontractorRepository
                .findAllByProviderSubcontractor_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(
                        providerSubcontractor.getIdProvider(), "Documento pessoa", true));
        documentSubcontractor.addAll(documentProviderSubcontractorRepository
                .findAllByProviderSubcontractor_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(
                        providerSubcontractor.getIdProvider(), "Treinamentos e certificações", true));

        List<DocumentEmployee> batch = new ArrayList<>(50);

        List<Contract> contracts = new ArrayList<>();
        contracts.add(contractProviderSubcontractor);

        for (Employee employee : employees) {
            Boolean existingDocumentCheck = false;
            List<DocumentEmployee> documentEmployeeList = documentEmployeeRepository.findAllByEmployee_IdEmployee(employee.getIdEmployee());

            for (DocumentProviderSubcontractor document : documentSubcontractor) {
                DocumentEmployee existingDocument = documentEmployeeList.stream()
                        .filter(de -> de.getTitle().equals(document.getTitle()))
                        .findFirst()
                        .orElse(null);
                if (existingDocument != null && document.getDocumentMatrix().getIsDocumentUnique()) {
                    existingDocument.getContracts().add(contractProviderSubcontractor);
                    existingDocumentCheck = true;
                } else if (existingDocument == null) {
                    batch.add(DocumentEmployee.builder()
                            .title(document.getTitle())
                            .status(Document.Status.PENDENTE)
                            .type(document.getType())
                            .isActive(true)
                            .documentMatrix(document.getDocumentMatrix())
                            .employee(employee)
                            .contracts(contracts)
                            .build());
                }

                if (batch.size() == 50) {
                    documentEmployeeRepository.saveAll(batch);
                    batch.clear();
                }

                if (existingDocumentCheck) {
                    documentEmployeeRepository.saveAll(documentEmployeeList);
                }
            }
        }

        if (!batch.isEmpty()) {
            documentEmployeeRepository.saveAll(batch);
        }
        log.info("Finished setup employee to contract subcontractor ✔️ {}, {}", employeeIds, contractProviderSubcontractorId);
    }

    public void setupRemoveEmployeeFromContract(String contractId, List<String> employeeIds) {
        log.info("Started setup remove employee from contract subcontractor ⌛ {}, {}", employeeIds, contractId);
        Contract contractProxy = contractRepository.findById(contractId)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        Contract contract = (Contract) Hibernate.unproxy(contractProxy);

        List<Employee> employees = employeeRepository.findAllById(employeeIds);

        for (Employee employee : employees) {
            if (employee.getContracts().isEmpty() && !employee.getSituation().equals(Employee.Situation.DESALOCADO)) {
                employee.setSituation(Employee.Situation.DESALOCADO);
            }
            List<DocumentEmployee> documentEmployeeList = documentEmployeeRepository.findAllByEmployee_IdEmployee(employee.getIdEmployee());
            for (DocumentEmployee documentEmployee : documentEmployeeList) {

                if (documentEmployee.getContracts().contains(contract) && documentEmployee.getContracts().size() == 1) {
                    documentEmployee.getContracts().remove(contract);
                    if (ChronoUnit.HOURS.between(documentEmployee.getAssignmentDate(), LocalDateTime.now()) < 24) {
                        documentEmployeeRepository.deleteById(documentEmployee.getIdDocumentation());
                    }
                } else if (documentEmployee.getContracts().contains(contract)) {
                    documentEmployee.getContracts().remove(contract);
                }
            }
            documentEmployeeRepository.saveAll(documentEmployeeList);
            employee.getContracts().remove(contract);
        }

        employeeRepository.saveAll(employees);

        log.info("Finished setup remove employee from contract subcontractor ✔️ {}, {}", employeeIds, contractId);
    }

    public void setupReplicateBranch(String branchId) {
        log.info("Started setup replicate branch ⌛ {}", branchId);
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new NotFoundException("Branch not found"));
        log.info("Started setup service type replicate branch ⌛ {}", branchId);
        List<ServiceTypeBranch> serviceTypeBranchBatch = new ArrayList<>(50);
        Branch base = branchRepository.findFirstByClient_IdClientAndIsActiveIsTrueAndBaseIsTrueOrderByCreationDate(branch.getClient().getIdClient());
        for (ServiceTypeBranch serviceTypeBranch : serviceTypeBranchRepository.findAllByBranch_IdBranch(base.getIdBranch())) {
            serviceTypeBranchBatch.add(
                    ServiceTypeBranch.builder()
                            .title(serviceTypeBranch.getTitle())
                            .risk(serviceTypeBranch.getRisk())
                            .branch(branch)
                            .build()
            );

            if (serviceTypeBranchBatch.size() == 50) {
                serviceTypeBranchRepository.saveAll(serviceTypeBranchBatch);
                serviceTypeBranchBatch.clear();
            }
        }
        if (!serviceTypeBranchBatch.isEmpty()) {
            serviceTypeBranchRepository.saveAll(serviceTypeBranchBatch);
            serviceTypeBranchBatch.clear();
        }

        log.info("Started setup document replicate branch ⌛ {}", branchId);
        List<DocumentBranch> batch = new ArrayList<>(50);
        for (var document : documentBranchRepository.findAllByBranch_IdBranch(base.getIdBranch())) {
            batch.add(DocumentBranch.builder()
                    .title(document.getTitle())
                    .type(document.getType())
                    .status(Document.Status.PENDENTE)
                    .isActive(true)
                    .branch(branch)
                    .documentMatrix(document.getDocumentMatrix())
                    .build());

            if (batch.size() == 50) {
                documentBranchRepository.saveAll(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            documentBranchRepository.saveAll(batch);
            batch.clear();
        }

        log.info("Started setup activity replicate branch ⌛ {}", branchId);
        List<Activity> activitybatch = new ArrayList<>(50);
        Map<String, Activity> repoToNewActivityMap = new HashMap<>();
        for (Activity activity : activityRepository.findAllByBranch_IdBranch(base.getIdBranch())) {
            Activity newActivity = Activity.builder()
                    .title(activity.getTitle())
                    .risk(activity.getRisk())
                    .branch(branch)
                    .build();
            activitybatch.add(newActivity);
            repoToNewActivityMap.put(activity.getIdActivity(), newActivity);

            if (activitybatch.size() == 50) {
                activityRepository.saveAll(activitybatch);
                activitybatch.clear();
            }
        }
        if (!activitybatch.isEmpty()) {
            activityRepository.saveAll(activitybatch);
            activitybatch.clear();
        }

        log.info("Started setup documents by activity replicate branch ⌛ {}", branchId);
        List<DocumentBranch> allBranchDocs = documentBranchRepository.findAllByBranch_IdBranch(base.getIdBranch());
        Map<String, DocumentBranch> matrixIdToBranchDocMap = allBranchDocs.stream()
                .filter(doc -> doc.getDocumentMatrix() != null)
                .collect(Collectors.toMap(doc -> doc.getDocumentMatrix().getIdDocument(), doc -> doc));

        List<ActivityDocuments> docs = activityDocumentRepository.findAllByDocumentBranch_Branch_IdBranch(base.getIdBranch());

        List<ActivityDocuments> newActivityDocs = new ArrayList<>();

        for (ActivityDocuments doc : docs) {
            Activity newActivity = repoToNewActivityMap.get(doc.getActivity().getIdActivity());

            DocumentMatrix matrix = doc.getDocumentBranch().getDocumentMatrix();
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
        log.info("Finished setup replicate branch ✔️ {}", branchId);
    }

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

    public void replicateUpdateActivity(String activityId, String title, Activity.Risk risk, List<String> branchIds) {
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

    public void replicateDeleteActivity(String clientId, String title, Activity.Risk risk, List<String> branchIds) {

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

    public void replicateUpdateServiceType(String serviceTypeBranchId, String title, ServiceType.Risk risk, List<String> branchIds) {
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

    public void replicateDeleteServiceType(String clientId, String title, ServiceType.Risk risk, List<String> branchIds) {
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
}
package bl.tech.realiza.services.queue.setup;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.ContractDocument;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.activity.ActivityDocuments;
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
import bl.tech.realiza.gateways.repositories.contracts.ContractDocumentRepository;
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
import bl.tech.realiza.gateways.requests.clients.branch.BranchCreateRequestDto;
import bl.tech.realiza.gateways.responses.clients.branches.BranchResponseDto;
import bl.tech.realiza.usecases.impl.users.profile.CrudProfileImpl;
import bl.tech.realiza.usecases.interfaces.clients.CrudBranch;
import bl.tech.realiza.usecases.interfaces.contracts.CrudServiceType;
import bl.tech.realiza.usecases.interfaces.contracts.activity.CrudActivity;
import bl.tech.realiza.usecases.interfaces.users.profile.CrudProfile;
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
    private final ContractDocumentRepository contractDocumentRepository;
    private final CrudBranch crudBranch;
    private final CrudProfile crudProfile;

    public void setupNewClient(String clientId, Boolean profilesFromRepo, List<String> activitiesIds) {
        log.info("Started setup client ⌛ {}", clientId);
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found while setup client"));;

        BranchResponseDto branch = crudBranch.save(BranchCreateRequestDto.builder()
                .name(client.getTradeName() != null
                        ? client.getTradeName()
                        : "Base")
                .cnpj(client.getCnpj())
                .cep(client.getCep())
                .state(client.getState())
                .city(client.getCity())
                .email(client.getEmail())
                .telephone(client.getTelephone())
                .address(client.getAddress())
                .number(client.getNumber())
                .base(true)
                .client(client.getIdClient())
                .build());

        if (profilesFromRepo) {
            crudProfile.transferFromRepoToClient(client.getIdClient());
        }
        crudServiceType.transferFromRepoToClient(client.getIdClient());
        crudBranch.setupBranch(branch.getIdBranch(), activitiesIds);
        log.info("Finished setup client ✔️ {}", clientId);
    }

    public void setupNewClientProfiles(String clientId) {
        log.info("Started setup client profiles ⌛ {}", clientId);

        Client client = null;
        int retries = 0;
        int maxRetries = 10;
        long delay = 500;
        while (client == null && retries < maxRetries) {
            try {
                int finalRetries = retries;
                client = clientRepository.findById(clientId)
                        .orElseThrow(() -> new NotFoundException("Client not found on attempt " + (finalRetries + 1)));
            } catch (NotFoundException e) {
                retries++;
                if (retries < maxRetries) {
                    log.warn("Client {} not found. Retrying in {}ms... ({}/{})", clientId, delay, retries, maxRetries);
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    log.error("Client {} not found after {} retries. Sending to DLQ.", clientId, maxRetries);
                    throw e; // Desiste e deixa a mensagem ir para a DLQ
                }
            }
        }

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
        log.info("Finished setup client profiles ✔️ {}", clientId);
    }

    public void setupBranch(String branchId, List<String> activityIds) {
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
        crudActivity.transferFromRepo(branch.getIdBranch(),activityIds);

        log.info("Finished setup branch ✔️ {}", branchId);
    }

    public void setupContractSupplier(String contractProviderSupplierId, List<String> activityIds) {
        log.info("Started setup contract supplier ⌛ {}", contractProviderSupplierId);
        List<Activity> activities = new ArrayList<>(List.of());
        List<String> idDocuments = new ArrayList<>(List.of());
        List<DocumentBranch> documentBranch;

        ContractProviderSupplier contractProviderSupplier = contractProviderSupplierRepository.findById(contractProviderSupplierId)
                .orElseThrow(() -> new NotFoundException("Contract not found"));
        Boolean hse = contractProviderSupplier.getHse();
        List<String> hseList = List.of("segurança do trabalho",
                "cadastro e certidões",
                "saude",
                "meio ambiente");
        Boolean labor = contractProviderSupplier.getLabor();

        if (hse && !activityIds.isEmpty()) {
            activities = activityRepository.findAllById(activityIds);
            if (activities.isEmpty()) {
                throw new NotFoundException("Activities not found");
            }

            for (Activity activity : activities) {
                List<ActivityDocuments> activityDocumentsList = activityDocumentRepository.findAllByActivity_IdActivity(activity.getIdActivity());
                for (ActivityDocuments activityDocument : activityDocumentsList) {
                    String type = activityDocument.getDocumentBranch().getType();
                    if ((hse && hseList.contains(type))
                            || (labor && type.equals("trabalhista"))
                            || type.equals("geral")) {
                        idDocuments.add(activityDocument.getDocumentBranch().getIdDocumentation());
                    }
                }
            }
        }

        if (!activities.isEmpty()) {
            contractProviderSupplier.setActivities(activities);
            contractProviderSupplierRepository.save(contractProviderSupplier);
        }

        documentBranch = documentBranchRepository.findAllById(idDocuments);

        ProviderSupplier finalNewProviderSupplier = contractProviderSupplier.getProviderSupplier();

        List<DocumentProviderSupplier> batch = new ArrayList<>(50);
        List<ContractDocument> auxiliarBatch = new ArrayList<>(50);
        List<DocumentProviderSupplier> existingDocuments = documentProviderSupplierRepository.findAllByProviderSupplier_IdProvider(finalNewProviderSupplier.getIdProvider());

        for (DocumentBranch document : documentBranch) {
            String newTitle = null;
            switch (document.getValidity()) {
                case INDEFINITE -> newTitle = document.getTitle();
                case WEEKLY -> newTitle = document.getWeeklyTitle();
                case MONTHLY -> newTitle = document.getMonthlyTitle();
                case ANNUAL -> newTitle = document.getAnnualTitle();
            }
            String finalNewTitle = newTitle;
            if (existingDocuments.stream()
                    .anyMatch(documentProviderSupplier -> documentProviderSupplier.getTitle().equals(finalNewTitle))) {
                if (document.getDocumentMatrix().getIsDocumentUnique()) {
                    // add contract to doc
                    Document existingDocument = (Document) existingDocuments.stream()
                            .findFirst()
                            .stream().filter(documentProviderSupplier -> documentProviderSupplier.getTitle().equals(finalNewTitle));

                    auxiliarBatch.add(ContractDocument.builder()
                            .document(existingDocument)
                            .contract(contractProviderSupplier)
                            .build());
                } else {
                    // cria doc
                    DocumentProviderSupplier newDocument = DocumentProviderSupplier.builder()
                        .title(newTitle)
                        .status(Document.Status.PENDENTE)
                        .type(document.getType())
                        .isActive(true)
                        .documentMatrix(document.getDocumentMatrix())
                        .providerSupplier(finalNewProviderSupplier)
                        .validity(document.getValidity())
                        .expirationDateUnit(document.getExpirationDateUnit())
                        .expirationDateAmount(document.getExpirationDateAmount())
                        .build();

                batch.add(newDocument);
                auxiliarBatch.add(ContractDocument.builder()
                        .document(newDocument)
                        .contract(contractProviderSupplier)
                        .build());
                }
            } else {
                // cria doc
                DocumentProviderSupplier newDocument = DocumentProviderSupplier.builder()
                        .title(newTitle)
                        .status(Document.Status.PENDENTE)
                        .type(document.getType())
                        .isActive(true)
                        .documentMatrix(document.getDocumentMatrix())
                        .providerSupplier(finalNewProviderSupplier)
                        .validity(document.getValidity())
                        .expirationDateUnit(document.getExpirationDateUnit())
                        .expirationDateAmount(document.getExpirationDateAmount())
                        .build();

                batch.add(newDocument);
                auxiliarBatch.add(ContractDocument.builder()
                        .document(newDocument)
                        .contract(contractProviderSupplier)
                        .build());
            }

            if (batch.size() == 50 || auxiliarBatch.size() == 50) {
                documentProviderSupplierRepository.saveAll(batch);
                contractProviderSupplierRepository.save(contractProviderSupplier);
                contractDocumentRepository.saveAll(auxiliarBatch);
                auxiliarBatch.clear();
                batch.clear();
            }
        }

        if (!batch.isEmpty() || !auxiliarBatch.isEmpty()) {
            documentProviderSupplierRepository.saveAll(batch);
            contractProviderSupplierRepository.save(contractProviderSupplier);
            contractDocumentRepository.saveAll(auxiliarBatch);
            auxiliarBatch.clear();
            batch.clear();
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
        Boolean hse = contractProviderSubcontractor.getHse();
        List<String> hseList = List.of("segurança do trabalho",
                "cadastro e certidões",
                "saude",
                "meio ambiente");
        Boolean labor = contractProviderSubcontractor.getLabor();

        if (hse && !activityIds.isEmpty()) {
            activities = activityRepository.findAllById(activityIds);
            if (activities.isEmpty()) {
                throw new NotFoundException("Activities not found");
            }

            for (Activity activity : activities) {
                List<ActivityDocuments> activityDocumentsList = activityDocumentRepository.findAllByActivity_IdActivity(activity.getIdActivity());
                for (ActivityDocuments activityDocument : activityDocumentsList) {
                    String type = activityDocument.getDocumentBranch().getType();
                    if ((hse && hseList.contains(type))
                            || (labor && type.equals("trabalhista"))
                            || type.equals("geral")) {
                        idDocuments.add(activityDocument.getDocumentBranch().getIdDocumentation());
                    }
                }
            }
        }

        if (!activities.isEmpty()) {
            contractProviderSubcontractor.setActivities(activities);
            contractProviderSubcontractorRepository.save(contractProviderSubcontractor);
        }

        documentSupplier = documentProviderSupplierRepository.findAllById(idDocuments);

        ProviderSubcontractor finalNewProviderSubcontractor = contractProviderSubcontractor.getProviderSubcontractor();
        List<DocumentProviderSubcontractor> batch = new ArrayList<>(50);
        List<DocumentProviderSubcontractor> existingDocuments = documentProviderSubcontractorRepository.findAllByProviderSubcontractor_IdProvider(finalNewProviderSubcontractor.getIdProvider());
        List<ContractDocument> auxiliarBatch = new ArrayList<>(50);

        for (DocumentProviderSupplier document : documentSupplier) {
            String newTitle = null;
            switch (document.getValidity()) {
                case INDEFINITE -> newTitle = document.getTitle();
                case WEEKLY -> newTitle = document.getWeeklyTitle();
                case MONTHLY -> newTitle = document.getMonthlyTitle();
                case ANNUAL -> newTitle = document.getAnnualTitle();
            }
            String finalNewTitle = newTitle;
            if (existingDocuments.stream()
                    .anyMatch(documentProviderSubcontractor -> documentProviderSubcontractor.getTitle().equals(finalNewTitle))) {
                if (document.getDocumentMatrix().getIsDocumentUnique()) {
                    // add contract to doc
                    Document existingDocument = (Document) existingDocuments.stream()
                            .findFirst()
                            .stream().filter(documentProviderSubcontractor -> documentProviderSubcontractor.getTitle().equals(finalNewTitle));

                    auxiliarBatch.add(ContractDocument.builder()
                            .document(existingDocument)
                            .contract(contractProviderSubcontractor)
                            .build());
                } else {
                    // cria doc
                    DocumentProviderSubcontractor newDocument = DocumentProviderSubcontractor.builder()
                        .title(newTitle)
                        .status(Document.Status.PENDENTE)
                        .type(document.getType())
                        .isActive(true)
                        .documentMatrix(document.getDocumentMatrix())
                        .validity(document.getValidity())
                        .providerSubcontractor(finalNewProviderSubcontractor)
                        .expirationDateUnit(document.getExpirationDateUnit())
                        .expirationDateAmount(document.getExpirationDateAmount())
                        .build();

                batch.add(newDocument);
                auxiliarBatch.add(ContractDocument.builder()
                        .document(newDocument)
                        .contract(contractProviderSubcontractor)
                        .build());
                }
            } else {
                // cria doc
                DocumentProviderSubcontractor newDocument = DocumentProviderSubcontractor.builder()
                        .title(newTitle)
                        .status(Document.Status.PENDENTE)
                        .type(document.getType())
                        .isActive(true)
                        .documentMatrix(document.getDocumentMatrix())
                        .validity(document.getValidity())
                        .providerSubcontractor(finalNewProviderSubcontractor)
                        .expirationDateUnit(document.getExpirationDateUnit())
                        .expirationDateAmount(document.getExpirationDateAmount())
                        .build();

                batch.add(newDocument);
                auxiliarBatch.add(ContractDocument.builder()
                        .document(newDocument)
                        .contract(contractProviderSubcontractor)
                        .build());
            }

            if (batch.size() == 50 || auxiliarBatch.size() == 50) {
                documentProviderSubcontractorRepository.saveAll(batch);
                contractProviderSubcontractorRepository.save(contractProviderSubcontractor);
                contractDocumentRepository.saveAll(auxiliarBatch);
                batch.clear();
            }
        }

        if (!batch.isEmpty() || !auxiliarBatch.isEmpty()) {
            documentProviderSubcontractorRepository.saveAll(batch);
            contractProviderSubcontractorRepository.save(contractProviderSubcontractor);
            contractDocumentRepository.saveAll(auxiliarBatch);
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
        List<ContractDocument> auxiliarBatch = new ArrayList<>(50);

        for (Employee employee : employees) {
            List<DocumentEmployee> documentEmployeeList = documentEmployeeRepository.findAllByEmployee_IdEmployee(employee.getIdEmployee());

            for (DocumentProviderSupplier document : documentSupplier) {
                String newTitle = null;
                switch (document.getValidity()) {
                    case INDEFINITE -> newTitle = document.getTitle();
                    case WEEKLY -> newTitle = document.getWeeklyTitle();
                    case MONTHLY -> newTitle = document.getMonthlyTitle();
                    case ANNUAL -> newTitle = document.getAnnualTitle();
                }
                String finalNewTitle = newTitle;
                DocumentEmployee existingDocument = documentEmployeeList.stream()
                        .filter(de -> de.getTitle().equals(finalNewTitle))
                        .findFirst()
                        .orElse(null);
                if (existingDocument != null && document.getDocumentMatrix().getIsDocumentUnique()) {
                    auxiliarBatch.add(ContractDocument.builder()
                                    .document(existingDocument)
                                    .contract(contractProviderSupplier)
                            .build());
                } else if (existingDocument == null) {
                    DocumentEmployee newDocument = DocumentEmployee.builder()
                            .title(newTitle)
                            .status(Document.Status.PENDENTE)
                            .type(document.getType())
                            .isActive(true)
                            .documentMatrix(document.getDocumentMatrix())
                            .employee(employee)
                            .validity(document.getValidity())
                            .expirationDateAmount(document.getExpirationDateAmount())
                            .expirationDateUnit(document.getExpirationDateUnit())
                            .build();
                    batch.add(newDocument);
                    auxiliarBatch.add(ContractDocument.builder()
                                    .document(newDocument)
                                    .contract(contractProviderSupplier)
                            .build());
                }
                if (batch.size() == 50 || auxiliarBatch.size() == 50) {
                    documentEmployeeRepository.saveAll(batch);
                    contractProviderSupplierRepository.save(contractProviderSupplier);
                    contractDocumentRepository.saveAll(auxiliarBatch);
                    batch.clear();
                    auxiliarBatch.clear();
                }
            }
            if (!batch.isEmpty() || !auxiliarBatch.isEmpty()) {
                documentEmployeeRepository.saveAll(batch);
                contractProviderSupplierRepository.save(contractProviderSupplier);
                contractDocumentRepository.saveAll(auxiliarBatch);
                batch.clear();
                auxiliarBatch.clear();
            }
        }

        if (!batch.isEmpty() || !auxiliarBatch.isEmpty()) {
            documentEmployeeRepository.saveAll(batch);
            contractProviderSupplierRepository.save(contractProviderSupplier);
            contractDocumentRepository.saveAll(auxiliarBatch);
            batch.clear();
            auxiliarBatch.clear();
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
        List<ContractDocument> auxiliarBatch = new ArrayList<>(50);

        for (Employee employee : employees) {
            List<DocumentEmployee> documentEmployeeList = documentEmployeeRepository.findAllByEmployee_IdEmployee(employee.getIdEmployee());

            for (DocumentProviderSubcontractor document : documentSubcontractor) {
                String newTitle = null;
                switch (document.getValidity()) {
                    case INDEFINITE -> newTitle = document.getTitle();
                    case WEEKLY -> newTitle = document.getWeeklyTitle();
                    case MONTHLY -> newTitle = document.getMonthlyTitle();
                    case ANNUAL -> newTitle = document.getAnnualTitle();
                }
                String finalNewTitle = newTitle;
                DocumentEmployee existingDocument = documentEmployeeList.stream()
                        .filter(de -> de.getTitle().equals(finalNewTitle))
                        .findFirst()
                        .orElse(null);
                if (existingDocument != null && document.getDocumentMatrix().getIsDocumentUnique()) {
                    auxiliarBatch.add(ContractDocument.builder()
                            .document(existingDocument)
                            .contract(contractProviderSubcontractor)
                            .build());
                } else if (existingDocument == null) {
                    DocumentEmployee newDocument = DocumentEmployee.builder()
                            .title(newTitle)
                            .status(Document.Status.PENDENTE)
                            .type(document.getType())
                            .isActive(true)
                            .documentMatrix(document.getDocumentMatrix())
                            .employee(employee)
                            .validity(document.getValidity())
                            .expirationDateAmount(document.getExpirationDateAmount())
                            .expirationDateUnit(document.getExpirationDateUnit())
                            .build();

                    batch.add(newDocument);
                    auxiliarBatch.add(ContractDocument.builder()
                            .document(newDocument)
                            .contract(contractProviderSubcontractor)
                            .build());
                }

                if (batch.size() == 50 || auxiliarBatch.size() == 50) {
                    documentEmployeeRepository.saveAll(batch);
                    contractProviderSubcontractorRepository.save(contractProviderSubcontractor);
                    contractDocumentRepository.saveAll(auxiliarBatch);
                    batch.clear();
                    auxiliarBatch.clear();
                }
            }
            if (!batch.isEmpty() || !auxiliarBatch.isEmpty()) {
                documentEmployeeRepository.saveAll(batch);
                contractProviderSubcontractorRepository.save(contractProviderSubcontractor);
                contractDocumentRepository.saveAll(auxiliarBatch);
                batch.clear();
                auxiliarBatch.clear();
            }
        }

        if (!batch.isEmpty() || !auxiliarBatch.isEmpty()) {
            documentEmployeeRepository.saveAll(batch);
            contractProviderSubcontractorRepository.save(contractProviderSubcontractor);
            contractDocumentRepository.saveAll(auxiliarBatch);
            batch.clear();
            auxiliarBatch.clear();
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
                List<ContractDocument> documentsToRemove = documentEmployee.getContractDocuments()
                        .stream()
                        .filter(cd -> cd.getContract().equals(contract))
                        .toList();

                if (!documentsToRemove.isEmpty()) {
                    documentEmployee.getContractDocuments().removeAll(documentsToRemove);
                    contract.getContractDocuments().removeIf(cd -> cd.getDocument().equals(documentEmployee));
                    contractDocumentRepository.deleteAll(documentsToRemove);
                    if (ChronoUnit.HOURS.between(documentEmployee.getAssignmentDate(), LocalDateTime.now()) < 24
                            && documentEmployee.getContractDocuments().isEmpty()) {
                        documentEmployeeRepository.deleteById(documentEmployee.getIdDocumentation());
                    }
                }
            }
            contractRepository.save(contract);
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
                    .validity(document.getValidity())
                    .expirationDateUnit(document.getExpirationDateUnit())
                    .expirationDateAmount(document.getExpirationDateAmount())
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
                    .activityRepo(activity.getActivityRepo())
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
}
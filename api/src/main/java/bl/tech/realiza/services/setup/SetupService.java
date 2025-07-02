package bl.tech.realiza.services.setup;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.activity.ActivityDocuments;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityDocumentRepository;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentBranchRepository;
import bl.tech.realiza.gateways.repositories.documents.employee.DocumentEmployeeRepository;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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

    public void setupNewClient(String clientId) {
        log.info("Started setup client ⌛ {}", clientId);
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found"));
        log.info("Finished setup client ✔️ {}", clientId);
//        crudServiceType.transferFromRepoToClient(client.getIdClient());
    }

    public void setupBranch(String branchId) {
        log.info("Started setup branch ⌛ {}", branchId);
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new NotFoundException("Branch not found"));
//        crudServiceType.transferFromClientToBranch(branch.getClient().getIdClient(), branch.getIdBranch());

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
        log.info("Finished setup branch ✔️ {}", branchId);
//        crudActivity.transferFromRepo(branch.getIdBranch());
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
}
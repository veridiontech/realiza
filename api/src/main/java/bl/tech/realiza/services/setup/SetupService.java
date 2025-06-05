package bl.tech.realiza.services.setup;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
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
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityDocumentRepository;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentBranchRepository;
import bl.tech.realiza.gateways.repositories.documents.employee.DocumentEmployeeRepository;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.usecases.interfaces.contracts.CrudServiceType;
import bl.tech.realiza.usecases.interfaces.contracts.activity.CrudActivity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SetupService {

    private final CrudServiceType crudServiceType;
    private final BranchRepository branchRepository;
    private final CrudActivity crudActivity;
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

    public void setupNewClient(String clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        Branch baseBranch = branchRepository.save(
                Branch.builder()
                        .name(client.getCorporateName() != null
                                ? client.getCorporateName() + " Base"
                                : "Base")
                        .cnpj(client.getCnpj())
                        .cep(client.getCep())
                        .state(client.getState())
                        .city(client.getCity())
                        .email(client.getEmail())
                        .telephone(client.getTelephone())
                        .address(client.getAddress())
                        .number(client.getNumber())
                        .client(client)
                        .build()
        );

        crudServiceType.transferFromRepoToClient(client.getIdClient());

        documentBranchRepository.saveAll(
                documentMatrixRepository.findAll()
                        .stream()
                        .map(documentMatrix -> DocumentBranch.builder()
                                .title(documentMatrix.getName())
                                .type(documentMatrix.getType())
                                .status(Document.Status.PENDENTE)
                                .isActive(true)
                                .branch(baseBranch)
                                .documentMatrix(documentMatrix)
                                .build())
                        .collect(Collectors.toList()));

        crudServiceType.transferFromClientToBranch(client.getIdClient(), baseBranch.getIdBranch());
        crudActivity.transferFromRepo(baseBranch.getIdBranch());
    }

    public void setupBranch(String branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new NotFoundException("Branch not found"));

        crudServiceType.transferFromClientToBranch(branch.getClient().getIdClient(), branch.getIdBranch());

        documentBranchRepository.saveAll(
                documentMatrixRepository.findAll()
                        .stream()
                        .map(documentMatrix -> DocumentBranch.builder()
                                .title(documentMatrix.getName())
                                .type(documentMatrix.getType())
                                .status(Document.Status.PENDENTE)
                                .isActive(true)
                                .branch(branch)
                                .documentMatrix(documentMatrix)
                                .build())
                        .collect(Collectors.toList()));

        crudActivity.transferFromRepo(branch.getIdBranch());
    }

    public void setupContractSupplier(String contractProviderSupplierId, List<String> activityIds) {
        List<Activity> activities = new ArrayList<>(List.of());
        List<String> idDocuments = new ArrayList<>(List.of());
        List<DocumentBranch> documentBranch;
        List<DocumentProviderSupplier> documentProviderSupplier = new ArrayList<>(List.of());

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

        documentBranch = documentBranchRepository.findAllById(idDocuments);

        ProviderSupplier finalNewProviderSupplier = contractProviderSupplier.getProviderSupplier();
        documentBranch.forEach(
                document -> documentProviderSupplier.add(DocumentProviderSupplier.builder()
                        .title(document.getTitle())
                        .status(Document.Status.PENDENTE)
                        .type(document.getType())
                        .isActive(true)
                        .documentMatrix(document.getDocumentMatrix())
                        .providerSupplier(finalNewProviderSupplier)
                        .build()));

        documentProviderSupplierRepository.saveAll(documentProviderSupplier);
    }

    public void setupContractSubcontractor(String contractProviderSubcontractorId, List<String> activityIds) {
        List<Activity> activities = new ArrayList<>(List.of());;
        List<DocumentProviderSupplier> documentSupplier;
        List<String> idDocuments = new ArrayList<>(List.of());
        List<DocumentProviderSubcontractor> documentProviderSubcontractor = new ArrayList<>(List.of());

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

        documentSupplier = documentProviderSupplierRepository.findAllById(idDocuments);

        ProviderSubcontractor finalNewProviderSubcontractor = contractProviderSubcontractor.getProviderSubcontractor();
        documentSupplier.forEach(
                document -> documentProviderSubcontractor.add(DocumentProviderSubcontractor.builder()
                        .title(document.getTitle())
                        .status(Document.Status.PENDENTE)
                        .type(document.getType())
                        .isActive(true)
                        .documentMatrix(document.getDocumentMatrix())
                        .providerSubcontractor(finalNewProviderSubcontractor)
                        .build()));

        documentProviderSubcontractorRepository.saveAll(documentProviderSubcontractor);
    }

    public void setupEmployeeToContractSupplier(String contractProviderSupplierId, List<String> employeeIds) {
        ContractProviderSupplier contractProviderSupplier = contractProviderSupplierRepository.findById(contractProviderSupplierId)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        List<Employee> employees = employeeRepository.findAllById(employeeIds);

        ProviderSupplier providerSupplier = contractProviderSupplier.getProviderSupplier();

        List<DocumentEmployee> documentEmployees = new ArrayList<>();

        List<DocumentProviderSupplier> documentSupplier = new ArrayList<>();

        documentSupplier.addAll(documentProviderSupplierRepository
                .findAllByProviderSupplier_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(
                        providerSupplier.getIdProvider(), "Documento pessoa", true));

        documentSupplier.addAll(documentProviderSupplierRepository
                .findAllByProviderSupplier_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(
                        providerSupplier.getIdProvider(), "Treinamentos e certificações", true));

        for (Employee employee : employees) {
            for (DocumentProviderSupplier document : documentSupplier) {
                documentEmployees.add(DocumentEmployee.builder()
                        .title(document.getTitle())
                        .status(Document.Status.PENDENTE)
                        .type(document.getType())
                        .isActive(true)
                        .documentMatrix(document.getDocumentMatrix())
                        .employee(employee)
                        .build());
            }
        }

        documentEmployeeRepository.saveAll(documentEmployees);
    }


    public void setupEmployeeToContractSubcontract(String contractProviderSubcontractorId, List<String> employeeIds) {
        ContractProviderSubcontractor contractProviderSubcontractor = contractProviderSubcontractorRepository.findById(contractProviderSubcontractorId)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        List<Employee> employees = employeeRepository.findAllById(employeeIds);
        ProviderSubcontractor providerSubcontractor = contractProviderSubcontractor.getProviderSubcontractor();

        List<DocumentEmployee> documentEmployees = new ArrayList<>();

        List<DocumentProviderSubcontractor> documentSubcontractor = new ArrayList<>();

        documentSubcontractor.addAll(documentProviderSubcontractorRepository
                .findAllByProviderSubcontractor_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(
                        providerSubcontractor.getIdProvider(),"Documento pessoa",true));

        documentSubcontractor.addAll(documentProviderSubcontractorRepository
                .findAllByProviderSubcontractor_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(
                        providerSubcontractor.getIdProvider(),"Treinamentos e certificações",true));

        for (Employee employee : employees) {
            for (DocumentProviderSubcontractor document : documentSubcontractor) {
                documentEmployees.add(DocumentEmployee.builder()
                        .title(document.getTitle())
                        .status(Document.Status.PENDENTE)
                        .type(document.getType())
                        .isActive(true)
                        .documentMatrix(document.getDocumentMatrix())
                        .employee(employee)
                        .build());
            }
        }

        documentEmployeeRepository.saveAll(documentEmployees);
    }
}

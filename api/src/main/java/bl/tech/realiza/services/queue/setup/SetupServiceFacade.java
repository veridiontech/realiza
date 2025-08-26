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
import bl.tech.realiza.usecases.interfaces.clients.CrudBranch;
import bl.tech.realiza.usecases.interfaces.contracts.CrudServiceType;
import bl.tech.realiza.usecases.interfaces.contracts.activity.CrudActivity;
import bl.tech.realiza.usecases.interfaces.users.profile.CrudProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
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
public class SetupServiceFacade {


    private final SetupService setupService;

    @Retryable(
            value = { NotFoundException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000)
    )
    public void setupNewClient(String clientId) {
        setupService.setupNewClient(clientId);
    }

    @Retryable(
            value = { NotFoundException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000)
    )
    public void setupNewClientProfiles(String clientId) {
        setupService.setupNewClientProfiles(clientId);
    }

    @Retryable(
            value = { NotFoundException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000)
    )
    public void setupBranch(String branchId) {
        setupService.setupBranch(branchId);
    }

    @Retryable(
            value = { NotFoundException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000)
    )
    public void setupContractSupplier(String contractProviderSupplierId, List<String> activityIds) {
        setupService.setupContractSupplier(contractProviderSupplierId, activityIds);
    }

    @Retryable(
            value = { NotFoundException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000)
    )
    public void setupContractSubcontractor(String contractProviderSubcontractorId, List<String> activityIds) {
        setupService.setupContractSubcontractor(contractProviderSubcontractorId, activityIds);
    }

    @Retryable(
            value = { NotFoundException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000)
    )
    public void setupEmployeeToContractSupplier(String contractProviderSupplierId, List<String> employeeIds) {
        setupService.setupEmployeeToContractSupplier(contractProviderSupplierId, employeeIds);
    }

    @Retryable(
            value = { NotFoundException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000)
    )
    public void setupEmployeeToContractSubcontract(String contractProviderSubcontractorId, List<String> employeeIds) {
        setupService.setupEmployeeToContractSubcontract(contractProviderSubcontractorId, employeeIds);
    }

    @Retryable(
            value = { NotFoundException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000)
    )
    public void setupRemoveEmployeeFromContract(String contractId, List<String> employeeIds) {
        setupService.setupRemoveEmployeeFromContract(contractId, employeeIds);
    }

    @Retryable(
            value = { NotFoundException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000)
    )
    public void setupReplicateBranch(String branchId) {
        setupService.setupReplicateBranch(branchId);
    }
}
package bl.tech.realiza.usecases.impl.contracts.contract;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.serviceType.ServiceType;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import bl.tech.realiza.domains.services.ItemManagement;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.contracts.EmployeeToContractRequestDto;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementContractRequestDto;
import bl.tech.realiza.gateways.responses.contracts.contract.ContractByBranchIdsResponseDto;
import bl.tech.realiza.gateways.responses.contracts.contract.ContractByEmployeeResponseDto;
import bl.tech.realiza.services.queue.setup.SetupMessage;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.services.auth.JwtService;
import bl.tech.realiza.services.queue.setup.SetupQueueProducer;
import bl.tech.realiza.usecases.interfaces.CrudItemManagement;
import bl.tech.realiza.usecases.interfaces.auditLogs.AuditLogService;
import bl.tech.realiza.usecases.interfaces.contracts.contract.CrudContract;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static bl.tech.realiza.domains.enums.AuditLogActionsEnum.*;
import static bl.tech.realiza.domains.enums.AuditLogTypeEnum.*;

@Service
@RequiredArgsConstructor
public class CrudContractImpl implements CrudContract {
    private final ContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final SetupQueueProducer setupQueueProducer;
    private final JwtService jwtService;
    private final CrudItemManagement crudItemManagement;

    @Override
    public String finishContractRequest(String idContract) {

        UserResponseDto requester = jwtService.extractAllClaims(jwtService.getTokenFromRequest());

        Contract contract = contractRepository.findById(idContract)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        crudItemManagement.saveContractSolicitation(ItemManagementContractRequestDto.builder()
                .solicitationType(ItemManagement.SolicitationType.FINISH)
                .idRequester(requester.getIdUser())
                .contractId(idContract)
                .build());

        contract.setStatus(ContractStatusEnum.FINISH_REQUESTED);
        contractRepository.save(contract);

        return "Contract finish requested";
    }

    @Override
    public String suspendContractRequest(String contractId) {
        UserResponseDto requester = jwtService.extractAllClaims(jwtService.getTokenFromRequest());

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        crudItemManagement.saveContractSolicitation(ItemManagementContractRequestDto.builder()
                .solicitationType(ItemManagement.SolicitationType.SUSPEND)
                .idRequester(requester.getIdUser())
                .contractId(contractId)
                .build());

        contract.setStatus(ContractStatusEnum.SUSPEND_REQUESTED);
        contractRepository.save(contract);

        return "Contract suspension requested";
    }

    @Override
    public String reactivateContractRequest(String contractId) {
        UserResponseDto requester = jwtService.extractAllClaims(jwtService.getTokenFromRequest());

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        crudItemManagement.saveContractSolicitation(ItemManagementContractRequestDto.builder()
                .solicitationType(ItemManagement.SolicitationType.REACTIVATION)
                .idRequester(requester.getIdUser())
                .contractId(contractId)
                .build());

        contract.setStatus(ContractStatusEnum.REACTIVATION_REQUESTED);
        contractRepository.save(contract);

        return "Contract reactivation requested";
    }

    @Override
    public String addEmployeeToContract(String idContract, EmployeeToContractRequestDto employeeToContractRequestDto) {
        Contract contractProxy = contractRepository.findById(idContract)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        Contract contract = (Contract) Hibernate.unproxy(contractProxy);

        switch (contract.getStatus()) {
            case PENDING -> throw new IllegalArgumentException("Contract is in pendent state");
            case SUSPENDED -> throw new IllegalArgumentException("Contract is in suspended state");
            case DENIED -> throw new IllegalArgumentException("Contract was denied");
        }

        List<Employee> employees = employeeRepository.findAllById(employeeToContractRequestDto.getEmployees());

        if (contract instanceof ContractProviderSupplier contractProviderSupplier) {
            for (Employee employee : employees) {
                if (!Objects.equals(contractProviderSupplier.getProviderSupplier().getIdProvider(), employee.getSupplier().getIdProvider())) {
                    throw new IllegalArgumentException("Contract provider does not match employee provider");
                }
                if (!employee.getContracts().contains(contract)) {
                    employee.getContracts().add(contract);
                }
                if (!employee.getSituation().equals(Employee.Situation.ALOCADO)) {
                    employee.setSituation(Employee.Situation.ALOCADO);
                }
            }

            setupQueueProducer.send(new SetupMessage("EMPLOYEE_CONTRACT_SUPPLIER",
                    null,
                    null,
                    contractProviderSupplier.getIdContract(),
                    null,
                    null,
                    null,
                    employees.stream().map(Employee::getIdEmployee).toList()));

        } else if (contract instanceof ContractProviderSubcontractor contractProviderSubcontractor) {
            for (Employee employee : employees) {
                if (!Objects.equals(contractProviderSubcontractor.getProviderSubcontractor().getIdProvider(), employee.getSubcontract().getIdProvider())) {
                    throw new IllegalArgumentException("Contract provider does not match employee provider");
                }
                if (!employee.getContracts().contains(contract)) {
                    employee.getContracts().add(contract);
                }
                if (!employee.getSituation().equals(Employee.Situation.ALOCADO)) {
                    employee.setSituation(Employee.Situation.ALOCADO);
                }
            }

            setupQueueProducer.send(new SetupMessage("EMPLOYEE_CONTRACT_SUBCONTRACT",
                    null,
                    null,
                    null,
                    contractProviderSubcontractor.getIdContract(),
                    null,
                    null,
                    employees.stream().map(Employee::getIdEmployee).toList()));

        } else {
            throw new NotFoundException("Invalid contract type");
        }

        employeeRepository.saveAll(employees);

        for (Employee employee : employees) {
            if (JwtService.getAuthenticatedUserId() != null) {
                User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                        .orElse(null);
                if (userResponsible != null) {
                    auditLogService.createAuditLog(
                            employee.getIdEmployee(),
                            CONTRACT,
                            userResponsible.getFullName() + " alocou colaborador "
                                    + employee.getFullName()
                                    + " ao contrato " + contract.getContractReference(),
                            null,
                            ALLOCATE,
                            userResponsible.getIdUser());

                    auditLogService.createAuditLog(
                            employee.getIdEmployee(),
                            EMPLOYEE,
                            userResponsible.getFullName() + " alocou colaborador "
                                    + employee.getFullName()
                                    + " ao contrato " + contract.getContractReference(),
                            null,
                            ALLOCATE,
                            userResponsible.getIdUser());
                }
            }
        }

        return "Employee added successfully";
    }

    @Override
    public String removeEmployeeToContract(String idContract, EmployeeToContractRequestDto employeeToContractRequestDto) {
        Contract contractProxy = contractRepository.findById(idContract)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        Contract contract = (Contract) Hibernate.unproxy(contractProxy);

        switch (contract.getIsActive()) {
            case PENDENTE -> throw new IllegalArgumentException("Contract is in pendent state");
            case NEGADO -> throw new IllegalArgumentException("Contract was denied");
        }

        List<Employee> employees = employeeRepository.findAllById(employeeToContractRequestDto.getEmployees());

        if (contract instanceof ContractProviderSupplier contractProviderSupplier) {
            for (Employee employee : employees) {
                if (!Objects.equals(contractProviderSupplier.getProviderSupplier().getIdProvider(), employee.getSupplier().getIdProvider())) {
                    throw new IllegalArgumentException("Contract provider does not match employee provider");
                }
            }
        } else if (contract instanceof ContractProviderSubcontractor contractProviderSubcontractor) {
            for (Employee employee : employees) {
                if (!Objects.equals(contractProviderSubcontractor.getProviderSubcontractor().getIdProvider(), employee.getSubcontract().getIdProvider())) {
                    throw new IllegalArgumentException("Contract provider does not match employee provider");
                }
            }
        } else {
            throw new NotFoundException("Invalid contract type");
        }

        setupQueueProducer.send(new SetupMessage("REMOVE_EMPLOYEE_CONTRACT",
                null,
                null,
                null,
                null,
                contract.getIdContract(),
                null,
                employees.stream().map(Employee::getIdEmployee).toList()));

        for (Employee employee : employees) {
            if (JwtService.getAuthenticatedUserId() != null) {
                User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                        .orElse(null);
                if (userResponsible != null) {
                    auditLogService.createAuditLog(
                            employee.getIdEmployee(),
                            CONTRACT,
                            userResponsible.getFullName() + " desalocou colaborador "
                                    + employee.getFullName()
                                    + " do contrato " + contract.getContractReference(),
                            null,
                            DEALLOCATE,
                            userResponsible.getIdUser());

                    auditLogService.createAuditLog(
                            employee.getIdEmployee(),
                            EMPLOYEE,
                            userResponsible.getFullName() + " desalocou colaborador "
                                    + employee.getFullName()
                                    + " do contrato " + contract.getContractReference(),
                            null,
                            DEALLOCATE,
                            userResponsible.getIdUser());
                }
            }
        }

        return "Employee added successfully";
    }

    @Override
    public Page<ContractByEmployeeResponseDto> getContractByEmployee(Pageable pageable, String idEmployee) {
        Page<Contract> contracts = contractRepository.findAllByEmployees_IdEmployee(idEmployee, pageable);

        return contracts.map(
                contract -> ContractByEmployeeResponseDto.builder()
                        .idContract(contract.getIdContract())
                        .contractReference(contract.getContractReference())
                        .build()
        );
    }

    @Override
    public List<ContractByBranchIdsResponseDto> getContractByBranchIds(List<String> branchIds) {
        return contractRepository.findAllByBranchIds(branchIds);
    }
}

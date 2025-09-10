package bl.tech.realiza.usecases.impl.contracts.contract;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.ContractEmployee;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import bl.tech.realiza.domains.enums.DocumentTypeEnum;
import bl.tech.realiza.domains.enums.PermissionSubTypeEnum;
import bl.tech.realiza.domains.enums.PermissionTypeEnum;
import bl.tech.realiza.domains.services.ItemManagement;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.contracts.ContractEmployeeRepository;
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
import bl.tech.realiza.usecases.interfaces.users.security.CrudPermission;
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
    private final CrudPermission crudPermission;
    private final ContractEmployeeRepository contractEmployeeRepository;

    @Override
    public String finishContractRequest(String idContract) {
        UserResponseDto requester = jwtService.extractAllClaims(jwtService.getTokenFromRequest());

        if (JwtService.getAuthenticatedUserId() != null) {
            User user = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElseThrow(() -> new NotFoundException("User not found"));

            if (crudPermission.hasPermission(user, PermissionTypeEnum.CONTRACT, PermissionSubTypeEnum.FINISH, DocumentTypeEnum.NONE)) {
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
            } else {
                return "Not enough permissions";
            }
        } else {
            return "Not authenticated user";
        }
    }

    @Override
    public String suspendContractRequest(String contractId) {
        UserResponseDto requester = jwtService.extractAllClaims(jwtService.getTokenFromRequest());

        if (JwtService.getAuthenticatedUserId() != null) {
            User user = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElseThrow(() -> new NotFoundException("User not found"));
            if (crudPermission.hasPermission(user, PermissionTypeEnum.CONTRACT, PermissionSubTypeEnum.SUSPEND, DocumentTypeEnum.NONE)) {
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
            } else {
                return "Not enough permissions";
            }
        } else {
            return "Not authenticated user";
        }

    }

    @Override
    public String reactivateContractRequest(String contractId) {
        UserResponseDto requester = jwtService.extractAllClaims(jwtService.getTokenFromRequest());

        if (JwtService.getAuthenticatedUserId() != null) {
            User user = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElseThrow(() -> new NotFoundException("User not found"));
            if (crudPermission.hasPermission(user, PermissionTypeEnum.CONTRACT, PermissionSubTypeEnum.SUSPEND, DocumentTypeEnum.NONE)) {
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
            } else {
                return "Not enough permissions";
            }
        } else {
            return "Not authenticated user";
        }

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
                if (employee.getContractEmployees().stream()
                        .noneMatch(contractEmployee ->
                                contractEmployee.getContract().equals(contract))) {
                    contractEmployeeRepository.save(ContractEmployee.builder()
                                    .contract(contract)
                                    .employee(employee)
                            .build());
                }
                if (!employee.getSituation().equals(Employee.Situation.ALOCADO)) {
                    employee.setSituation(Employee.Situation.ALOCADO);
                }
            }

            setupQueueProducer.send(SetupMessage.builder()
                            .type("EMPLOYEE_CONTRACT_SUPPLIER")
                            .contractSupplierId(contractProviderSupplier.getIdContract())
                            .employeeIds(employees.stream().map(Employee::getIdEmployee).toList())
                    .build());

        } else if (contract instanceof ContractProviderSubcontractor contractProviderSubcontractor) {
            for (Employee employee : employees) {
                if (!Objects.equals(contractProviderSubcontractor.getProviderSubcontractor().getIdProvider(), employee.getSubcontract().getIdProvider())) {
                    throw new IllegalArgumentException("Contract provider does not match employee provider");
                }
                if (employee.getContractEmployees().stream()
                        .noneMatch(contractEmployee ->
                                contractEmployee.getContract().equals(contract))) {
                    contractEmployeeRepository.save(ContractEmployee.builder()
                            .contract(contract)
                            .employee(employee)
                            .build());
                }
                if (!employee.getSituation().equals(Employee.Situation.ALOCADO)) {
                    employee.setSituation(Employee.Situation.ALOCADO);
                }
            }

            setupQueueProducer.send(SetupMessage.builder()
                    .type("EMPLOYEE_CONTRACT_SUBCONTRACT")
                    .contractSubcontractorId(contractProviderSubcontractor.getIdContract())
                    .employeeIds(employees.stream().map(Employee::getIdEmployee).toList())
                    .build());

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

        setupQueueProducer.send(SetupMessage.builder()
                .type("REMOVE_EMPLOYEE_CONTRACT")
                .contractId(contract.getIdContract())
                .employeeIds(employees.stream().map(Employee::getIdEmployee).toList())
                .build());

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
        Page<Contract> contracts = contractRepository.findAllByEmployeeContracts_Employee_IdEmployee(idEmployee, pageable);

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

    @Override
    public String integrateEmployeeToContract(String contractId, String employeeId) {
        ContractEmployee contractEmployee = contractEmployeeRepository.findByContract_IdContractAndEmployee_IdEmployee(contractId,employeeId)
                .orElse(null);
        if (contractEmployee != null) {
            if (contractEmployee.getIntegrated() != null && contractEmployee.getIntegrated()) {
                throw new IllegalStateException("Employee already integrated");
            }
            contractEmployee.setIntegrated(true);
            contractEmployeeRepository.save(contractEmployee);
        } else {
            throw new IllegalStateException("Employee not assigned to the contract");
        }
        return "Employee integrated to the contract";
    }
}

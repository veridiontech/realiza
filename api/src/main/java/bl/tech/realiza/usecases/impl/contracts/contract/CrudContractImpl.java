package bl.tech.realiza.usecases.impl.contracts.contract;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.serviceType.ServiceType;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.contracts.EmployeeToContractRequestDto;
import bl.tech.realiza.gateways.responses.contracts.contract.ContractByEmployeeResponseDto;
import bl.tech.realiza.gateways.responses.queue.SetupMessage;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.services.auth.JwtService;
import bl.tech.realiza.services.queue.SetupAsyncQueueProducer;
import bl.tech.realiza.usecases.interfaces.auditLogs.AuditLogService;
import bl.tech.realiza.usecases.interfaces.contracts.contract.CrudContract;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static bl.tech.realiza.domains.contract.Contract.IsActive.*;
import static bl.tech.realiza.domains.enums.AuditLogActionsEnum.*;
import static bl.tech.realiza.domains.enums.AuditLogTypeEnum.*;

@Service
@RequiredArgsConstructor
public class CrudContractImpl implements CrudContract {
    private final ContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogServiceImpl;
    private final SetupAsyncQueueProducer setupAsyncQueueProducer;
    private final JwtService jwtService;

    @Override
    public String finishContract(String idContract) {

        UserResponseDto requester = jwtService.extractAllClaims(jwtService.getTokenFromRequest());

        if (requester.getAdmin()
                || requester.getRole().equals(User.Role.ROLE_REALIZA_BASIC)
                || requester.getRole().equals(User.Role.ROLE_REALIZA_PLUS)
                || requester.getManager()) {

            Contract contract = contractRepository.findById(idContract)
                    .orElseThrow(() -> new NotFoundException("Contract not found"));
            if (requester.getContractAccess().contains(contract.getIdContract())) {
                contract.setFinished(true);
                contract.setEndDate(Date.valueOf(LocalDate.now()));

                contract = contractRepository.save(contract);

                if (JwtService.getAuthenticatedUserId() != null) {
                    User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                            .orElse(null);
                    if (userResponsible != null) {
                        auditLogServiceImpl.createAuditLog(
                                contract.getIdContract(),
                                CONTRACT,
                                userResponsible.getEmail() + " finalizou contrato " + contract.getContractReference(),
                                null,
                                FINISH,
                                userResponsible.getIdUser());
                    }
                }
                return "Contract finished successfully";
            }
        }
        throw new IllegalArgumentException("User don't have permission to finish a contract");
    }

    @Override
    public String suspendContract(String contractId) {
        UserResponseDto requester = jwtService.extractAllClaims(jwtService.getTokenFromRequest());

        if (requester.getAdmin()
                || requester.getRole().equals(User.Role.ROLE_REALIZA_BASIC)
                || requester.getRole().equals(User.Role.ROLE_REALIZA_PLUS)
                || requester.getManager()) {
            Contract contract = contractRepository.findById(contractId)
                    .orElseThrow(() -> new NotFoundException("Contract not found"));
            if (requester.getContractAccess().contains(contract.getIdContract())) {
                contract.setIsActive(SUSPENSO);
                contract.setEndDate(Date.valueOf(LocalDate.now()));

                contract = contractRepository.save(contract);

                if (JwtService.getAuthenticatedUserId() != null) {
                    User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                            .orElse(null);
                    if (userResponsible != null) {
                        auditLogServiceImpl.createAuditLog(
                                contract.getIdContract(),
                                CONTRACT,
                                userResponsible.getEmail() + " suspendeu contrato " + contract.getContractReference(),
                                null,
                                UPDATE,
                                userResponsible.getIdUser());
                    }
                }

                return "Contract suspended successfully";
            }
        }
        throw new IllegalArgumentException("User don't have permission to suspend a contract");
    }

    @Override
    public String addEmployeeToContract(String idContract, EmployeeToContractRequestDto employeeToContractRequestDto) {
        Contract contractProxy = contractRepository.findById(idContract)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        Contract contract = (Contract) Hibernate.unproxy(contractProxy);

        switch (contract.getIsActive()) {
            case PENDENTE -> throw new IllegalArgumentException("Contract is in pendent state");
            case SUSPENSO -> throw new IllegalArgumentException("Contract is in suspended state");
            case NEGADO -> throw new IllegalArgumentException("Contract was denied");
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

            setupAsyncQueueProducer.sendSetup(new SetupMessage("EMPLOYEE_CONTRACT_SUPPLIER",
                    null,
                    null,
                    contractProviderSupplier.getIdContract(),
                    null,
                    null,
                    null,
                    employees.stream().map(Employee::getIdEmployee).toList(),
                    null,
                    null,
                    null,
                    null,
                    Activity.Risk.LOW,
                    ServiceType.Risk.LOW));

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

            setupAsyncQueueProducer.sendSetup(new SetupMessage("EMPLOYEE_CONTRACT_SUBCONTRACT",
                    null,
                    null,
                    null,
                    contractProviderSubcontractor.getIdContract(),
                    null,
                    null,
                    employees.stream().map(Employee::getIdEmployee).toList(),
                    null,
                    null,
                    null,
                    null,
                    Activity.Risk.LOW,
                    ServiceType.Risk.LOW));

        } else {
            throw new NotFoundException("Invalid contract type");
        }

        employeeRepository.saveAll(employees);

        for (Employee employee : employees) {
            if (JwtService.getAuthenticatedUserId() != null) {
                User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                        .orElse(null);
                if (userResponsible != null) {
                    auditLogServiceImpl.createAuditLog(
                            employee.getIdEmployee(),
                            CONTRACT,
                            userResponsible.getEmail() + " alocou colaborador " + employee.getName()
                            + " ao contrato " + contract.getContractReference(),
                            null,
                            ALLOCATE,
                            userResponsible.getIdUser());

                    auditLogServiceImpl.createAuditLog(
                            employee.getIdEmployee(),
                            EMPLOYEE,
                            userResponsible.getEmail() + " alocou colaborador " + employee.getName()
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

        setupAsyncQueueProducer.sendSetup(new SetupMessage("REMOVE_EMPLOYEE_CONTRACT",
                null,
                null,
                null,
                null,
                contract.getIdContract(),
                null,
                employees.stream().map(Employee::getIdEmployee).toList(),
                null,
                null,
                null,
                null,
                Activity.Risk.LOW,
                ServiceType.Risk.LOW));

        for (Employee employee : employees) {
            if (JwtService.getAuthenticatedUserId() != null) {
                User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                        .orElse(null);
                if (userResponsible != null) {
                    auditLogServiceImpl.createAuditLog(
                            employee.getIdEmployee(),
                            CONTRACT,
                            userResponsible.getEmail() + " desalocou colaborador " + employee.getName()
                                    + " do contrato " + contract.getContractReference(),
                            null,
                            DEALLOCATE,
                            userResponsible.getIdUser());

                    auditLogServiceImpl.createAuditLog(
                            employee.getIdEmployee(),
                            EMPLOYEE,
                            userResponsible.getEmail() + " desalocou colaborador " + employee.getName()
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
}

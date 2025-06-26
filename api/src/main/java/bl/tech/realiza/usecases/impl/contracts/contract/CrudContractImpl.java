package bl.tech.realiza.usecases.impl.contracts.contract;

import bl.tech.realiza.domains.auditLogs.contract.AuditLogContract;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.documents.employee.DocumentEmployeeRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.contracts.EmployeeToContractRequestDto;
import bl.tech.realiza.gateways.responses.contracts.contract.ContractByEmployeeResponseDto;
import bl.tech.realiza.gateways.responses.queue.SetupMessage;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static bl.tech.realiza.domains.contract.Contract.IsActive.*;

@Service
@RequiredArgsConstructor
public class CrudContractImpl implements CrudContract {
    private final ContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final DocumentProviderSupplierRepository documentProviderSupplierRepository;
    private final DocumentEmployeeRepository documentEmployeeRepository;
    private final DocumentProviderSubcontractorRepository documentProviderSubcontractorRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogServiceImpl;
    private final SetupAsyncQueueProducer setupAsyncQueueProducer;

    @Override
    public String finishContract(String idContract) {
        Contract contract = contractRepository.findById(idContract)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        contract.setFinished(true);
        contract.setEndDate(Date.valueOf(LocalDate.now()));

        contract = contractRepository.save(contract);

        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            if (userResponsible != null) {
                auditLogServiceImpl.createAuditLogContract(
                        contract,
                        userResponsible.getEmail() + " finished contract " + contract.getContractReference(),
                        AuditLogContract.AuditLogContractActions.FINISH,
                        userResponsible);
            }
        }

        return "Contract finished successfully";
    }

    @Override
    public String suspendContract(String contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        contract.setIsActive(SUSPENSO);
        contract.setEndDate(Date.valueOf(LocalDate.now()));

        contract = contractRepository.save(contract);

        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            if (userResponsible != null) {
                auditLogServiceImpl.createAuditLogContract(
                        contract,
                        userResponsible.getEmail() + " suspended contract " + contract.getContractReference(),
                        AuditLogContract.AuditLogContractActions.UPDATE,
                        userResponsible);
            }
        }

        return "Contract suspended successfully";
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
            setupAsyncQueueProducer.sendSetup(new SetupMessage("EMPLOYEE_CONTRACT_SUPPLIER", null, null, contractProviderSupplier.getIdContract(), null, null, employees.stream().map(Employee::getIdEmployee).toList()));
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
            setupAsyncQueueProducer.sendSetup(new SetupMessage("EMPLOYEE_CONTRACT_SUBCONTRACT", null, null, null, contractProviderSubcontractor.getIdContract(), null, employees.stream().map(Employee::getIdEmployee).toList()));
        } else {
            throw new NotFoundException("Invalid contract type");
        }

        employeeRepository.saveAll(employees);

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

                employee.getContracts().remove(contract);
            }

        } else if (contract instanceof ContractProviderSubcontractor contractProviderSubcontractor) {
            for (Employee employee : employees) {
                if (!Objects.equals(contractProviderSubcontractor.getProviderSubcontractor().getIdProvider(), employee.getSubcontract().getIdProvider())) {
                    throw new IllegalArgumentException("Contract provider does not match employee provider");
                }

                employee.getContracts().remove(contract);
            }
        } else {
            throw new NotFoundException("Invalid contract type");
        }

        for (Employee employee : employees) {
            if (employee.getContracts().isEmpty() && !employee.getSituation().equals(Employee.Situation.DESALOCADO)) {
                employee.setSituation(Employee.Situation.DESALOCADO);
            }
        }

        contractRepository.save(contract);

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

package bl.tech.realiza.usecases.impl.contracts.contract;

import bl.tech.realiza.domains.auditLogs.contract.AuditLogContract;
import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogBranch;
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
import bl.tech.realiza.services.auth.JwtService;
import bl.tech.realiza.usecases.impl.auditLogs.AuditLogServiceImpl;
import bl.tech.realiza.usecases.interfaces.auditLogs.AuditLogService;
import bl.tech.realiza.usecases.interfaces.contracts.contract.CrudContract;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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

    @Override
    public String finishContract(String idContract) {
        Contract contract = contractRepository.findById(idContract)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        contract.setFinished(true);

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
    public String addEmployeeToContract(String idContract, EmployeeToContractRequestDto employeeToContractRequestDto) {
        List<DocumentEmployee> documentEmployee = new java.util.ArrayList<>(List.of());
        Contract contractProxy = contractRepository.findById(idContract)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        Contract contract = (Contract) Hibernate.unproxy(contractProxy);

        List<Employee> employees = employeeRepository.findAllById(employeeToContractRequestDto.getEmployees());

        if (contract instanceof ContractProviderSupplier contractProviderSupplier) {
            ProviderSupplier providerSupplier = contractProviderSupplier.getProviderSupplier();
            List<DocumentProviderSupplier> personalDocuments = documentProviderSupplierRepository
                    .findAllByProviderSupplier_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(
                            providerSupplier.getIdProvider(),"Documento pessoa",true);
            List<DocumentProviderSupplier> trainingAndCertificates = documentProviderSupplierRepository
                    .findAllByProviderSupplier_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(
                            providerSupplier.getIdProvider(),"Treinamentos e certificações",true);
            employees.forEach(
                employee -> {
                    if (!Objects.equals(contractProviderSupplier.getProviderSupplier().getIdProvider(),
                            employee.getSupplier().getIdProvider())) {
                        throw new IllegalArgumentException("Contract provider does not match employee provider");
                    }
                    personalDocuments.forEach(
                            documentProviderSupplier -> {
                                documentEmployee.add(DocumentEmployee.builder()
                                                .title(documentProviderSupplier.getTitle())
                                                .status(Document.Status.PENDENTE)
                                                .type(documentProviderSupplier.getType())
                                                .isActive(true)
                                                .documentMatrix(documentProviderSupplier.getDocumentMatrix())
                                                .employee(employee)
                                        .build());
                            }
                    );
                    trainingAndCertificates.forEach(
                            documentProviderSupplier -> {
                                documentEmployee.add(DocumentEmployee.builder()
                                        .title(documentProviderSupplier.getTitle())
                                        .status(Document.Status.PENDENTE)
                                        .type(documentProviderSupplier.getType())
                                        .isActive(true)
                                        .documentMatrix(documentProviderSupplier.getDocumentMatrix())
                                        .employee(employee)
                                        .build());
                            }
                    );
                }
            );

        } else if (contract instanceof ContractProviderSubcontractor contractProviderSubcontractor) {
            ProviderSubcontractor subcontractor = contractProviderSubcontractor.getProviderSubcontractor();
            List<DocumentProviderSubcontractor> personalDocuments = documentProviderSubcontractorRepository
                    .findAllByProviderSubcontractor_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(
                            subcontractor.getIdProvider(),"Documento pessoa",true);
            List<DocumentProviderSubcontractor> trainingAndCertificates = documentProviderSubcontractorRepository
                    .findAllByProviderSubcontractor_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(
                            subcontractor.getIdProvider(),"Treinamentos e certificações",true);
            employees.forEach(
                employee -> {
                    if (!Objects.equals(contractProviderSubcontractor.getProviderSubcontractor().getIdProvider(),
                            employee.getSubcontract().getIdProvider())) {
                        throw new IllegalArgumentException("Contract provider does not match employee provider");
                    }
                    personalDocuments.forEach(
                            documentProviderSubcontractor -> {
                                documentEmployee.add(DocumentEmployee.builder()
                                        .title(documentProviderSubcontractor.getTitle())
                                        .status(Document.Status.PENDENTE)
                                        .type(documentProviderSubcontractor.getType())
                                        .isActive(true)
                                        .documentMatrix(documentProviderSubcontractor.getDocumentMatrix())
                                        .employee(employee)
                                        .build());
                            }
                    );
                    trainingAndCertificates.forEach(
                            documentProviderSubcontractor -> {
                                documentEmployee.add(DocumentEmployee.builder()
                                        .title(documentProviderSubcontractor.getTitle())
                                        .status(Document.Status.PENDENTE)
                                        .type(documentProviderSubcontractor.getType())
                                        .isActive(true)
                                        .documentMatrix(documentProviderSubcontractor.getDocumentMatrix())
                                        .employee(employee)
                                        .build());
                            }
                    );
                }
            );

        } else {
            throw new NotFoundException("Invalid contract type");
        }

        employees.forEach(
                employee -> {
                    employee.getContracts().add(contract);
                }
        );
        employeeRepository.saveAll(employees);
        documentEmployeeRepository.saveAll(documentEmployee);

        return "Employee added successfully";
    }

    @Override
    public String removeEmployeeToContract(String idContract, EmployeeToContractRequestDto employeeToContractRequestDto) {
        Contract contractProxy = contractRepository.findById(idContract)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        Contract contract = (Contract) Hibernate.unproxy(contractProxy);

        List<Employee> employees = employeeRepository.findAllById(employeeToContractRequestDto.getEmployees());

        if (contract instanceof ContractProviderSupplier contractProviderSupplier) {
            employees.forEach(
                employee -> {
                    if (!Objects.equals(contractProviderSupplier.getProviderSupplier().getIdProvider(),
                            employee.getSupplier().getIdProvider())) {
                        throw new IllegalArgumentException("Contract provider does not match employee provider");
                    }
                }
            );

        } else if (contract instanceof ContractProviderSubcontractor contractProviderSubcontractor) {
            employees.forEach(
                employee -> {
                    if (!Objects.equals(contractProviderSubcontractor.getProviderSubcontractor().getIdProvider(),
                            employee.getSubcontract().getIdProvider())) {
                        throw new IllegalArgumentException("Contract provider does not match employee provider");
                    }
                }
            );
        } else {
            throw new NotFoundException("Invalid contract type");
        }

        contract.getEmployees().removeAll(employees);
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

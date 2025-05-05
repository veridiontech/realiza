package bl.tech.realiza.usecases.impl.contracts.contract;

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
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.documents.employee.DocumentEmployeeRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.usecases.interfaces.contracts.contract.CrudContract;
import lombok.RequiredArgsConstructor;
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

    @Override
    public String finishContract(String idContract) {
        Contract contract = contractRepository.findById(idContract)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        contract.setFinished(true);

        contractRepository.save(contract);

        return "Contract finished successfully";
    }

    @Override
    public String addEmployeeToContract(String idContract, String idEmployee) {
        List<DocumentEmployee> documentEmployee = new java.util.ArrayList<>(List.of());
        Contract contract = contractRepository.findById(idContract)
                .orElseThrow(() -> new NotFoundException("Contract not found"));
        Employee employee = employeeRepository.findById(idEmployee)
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        if (contract instanceof ContractProviderSupplier contractProviderSupplier) {
            if (!Objects.equals(contractProviderSupplier.getProviderSupplier().getIdProvider(),
                    employee.getSupplier().getIdProvider())) {
                throw new IllegalArgumentException("Contract provider does not match employee provider");
            }
            ProviderSupplier providerSupplier = contractProviderSupplier.getProviderSupplier();
            List<DocumentProviderSupplier> personalDocuments = documentProviderSupplierRepository
                    .findAllByProviderSupplier_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(
                            providerSupplier.getIdProvider(),"Documento pessoa",true);
            List<DocumentProviderSupplier> trainingAndCertificates = documentProviderSupplierRepository
                    .findAllByProviderSupplier_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(
                            providerSupplier.getIdProvider(),"Treinamentos e certificações",true);
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
        } else if (contract instanceof ContractProviderSubcontractor contractProviderSubcontractor) {
            if (!Objects.equals(contractProviderSubcontractor.getProviderSubcontractor().getIdProvider(),
                    employee.getSubcontract().getIdProvider())) {
                throw new IllegalArgumentException("Contract provider does not match employee provider");
            }
            ProviderSubcontractor subcontractor = contractProviderSubcontractor.getProviderSubcontractor();
            List<DocumentProviderSubcontractor> personalDocuments = documentProviderSubcontractorRepository
                    .findAllByProviderSubcontractor_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(
                            subcontractor.getIdProvider(),"Documento pessoa",true);
            List<DocumentProviderSubcontractor> trainingAndCertificates = documentProviderSubcontractorRepository
                    .findAllByProviderSubcontractor_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(
                            subcontractor.getIdProvider(),"Treinamentos e certificações",true);
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
        } else {
            throw new NotFoundException("Invalid contract type");
        }

        contract.getEmployees().add(employee);
        contractRepository.save(contract);
        documentEmployeeRepository.saveAll(documentEmployee);

        return "Employee added successfully";
    }

    @Override
    public String removeEmployeeToContract(String idContract, String idEmployee) {
        Contract contract = contractRepository.findById(idContract)
                .orElseThrow(() -> new NotFoundException("Contract not found"));
        Employee employee = employeeRepository.findById(idEmployee)
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        if (contract instanceof ContractProviderSupplier contractProviderSupplier) {
            if (!Objects.equals(contractProviderSupplier.getProviderSupplier().getIdProvider(),
                    employee.getSupplier().getIdProvider())) {
                throw new IllegalArgumentException("Contract provider does not match employee provider");
            }

        } else if (contract instanceof ContractProviderSubcontractor contractProviderSubcontractor) {
            if (!Objects.equals(contractProviderSubcontractor.getProviderSubcontractor().getIdProvider(),
                    employee.getSubcontract().getIdProvider())) {
                throw new IllegalArgumentException("Contract provider does not match employee provider");
            }
        } else {
            throw new NotFoundException("Invalid contract type");
        }

        contract.getEmployees().remove(employee);
        contractRepository.save(contract);

        return "Employee added successfully";
    }
}

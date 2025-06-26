package bl.tech.realiza.usecases.impl.documents.contract;

import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.responses.documents.ContractDocumentAndEmployeeResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.contract.CrudDocumentContract;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CrudDocumentContractImpl implements CrudDocumentContract {
    private final ContractRepository contractRepository;

    @Override
    public ContractDocumentAndEmployeeResponseDto getDocumentAndEmployeeByContractId(String id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found"));

        List<ContractDocumentAndEmployeeResponseDto.DocumentDto> documentDtos = new ArrayList<>();
        List<ContractDocumentAndEmployeeResponseDto.EmployeeDto> employeeDtos = new ArrayList<>();

        for (Employee employee : contract.getEmployees()) {
            employeeDtos.add(ContractDocumentAndEmployeeResponseDto.EmployeeDto.builder()
                    .id(employee.getIdEmployee())
                    .name(employee.getName()
                            + (employee.getSurname() != null
                            ? " " + employee.getSurname() : ""))
                    .cboTitle(employee.getCbo() != null
                            ? employee.getCbo().getTitle() : null)
                    .build());

            for (DocumentEmployee documentEmployee : employee.getDocumentEmployees()) {
                documentDtos.add(ContractDocumentAndEmployeeResponseDto.DocumentDto.builder()
                                .id(documentEmployee.getIdDocumentation())
                                .title(documentEmployee.getTitle())
                                .status(documentEmployee.getStatus())
                                .ownerName(documentEmployee.getEmployee().getName()
                                        + (documentEmployee.getEmployee().getSurname() != null
                                        ? " " + documentEmployee.getEmployee().getSurname() : ""))
                                .enterprise(false)
                                .build());
            }
        }

        employeeDtos.sort(Comparator
                .comparing(ContractDocumentAndEmployeeResponseDto.EmployeeDto::getName, String.CASE_INSENSITIVE_ORDER));

        if (contract instanceof ContractProviderSupplier contractProviderSupplier) {
            if (contractProviderSupplier.getProviderSupplier() != null) {
                if (contractProviderSupplier.getProviderSupplier().getDocumentProviderSuppliers() != null) {
                    for (DocumentProviderSupplier documentProviderSupplier : contractProviderSupplier.getProviderSupplier().getDocumentProviderSuppliers()) {
                        documentDtos.add(ContractDocumentAndEmployeeResponseDto.DocumentDto.builder()
                                    .id(documentProviderSupplier.getIdDocumentation())
                                    .title(documentProviderSupplier.getTitle())
                                    .status(documentProviderSupplier.getStatus())
                                    .ownerName(documentProviderSupplier.getProviderSupplier() != null
                                            ? documentProviderSupplier.getProviderSupplier().getCorporateName()
                                            : null)
                                    .enterprise(true)
                                .build());
                    }
                }
            }

            documentDtos.sort(Comparator
                    .comparing(ContractDocumentAndEmployeeResponseDto.DocumentDto::getEnterprise).reversed()
                    .thenComparing(ContractDocumentAndEmployeeResponseDto.DocumentDto::getTitle, String.CASE_INSENSITIVE_ORDER));

            return ContractDocumentAndEmployeeResponseDto.builder()
                    .enterpriseName(contractProviderSupplier.getProviderSupplier() != null
                            ? contractProviderSupplier.getProviderSupplier().getCorporateName()
                            : null)
                    .documentDtos(documentDtos)
                    .employeeDtos(employeeDtos)
                    .build();

        } else if (contract instanceof ContractProviderSubcontractor contractProviderSubcontractor) {
            if (contractProviderSubcontractor.getProviderSubcontractor() != null) {
                if (contractProviderSubcontractor.getProviderSubcontractor().getDocumentProviderSubcontractors() != null) {
                    for (DocumentProviderSubcontractor documentProviderSubcontractor : contractProviderSubcontractor.getProviderSubcontractor().getDocumentProviderSubcontractors()) {
                        documentDtos.add(ContractDocumentAndEmployeeResponseDto.DocumentDto.builder()
                                        .id(documentProviderSubcontractor.getIdDocumentation())
                                        .title(documentProviderSubcontractor.getTitle())
                                        .status(documentProviderSubcontractor.getStatus())
                                        .ownerName(documentProviderSubcontractor.getProviderSubcontractor() != null
                                                ? documentProviderSubcontractor.getProviderSubcontractor().getCorporateName()
                                                : null)
                                        .enterprise(true)
                                .build());
                    }
                }
            }

            return ContractDocumentAndEmployeeResponseDto.builder()
                    .enterpriseName(contractProviderSubcontractor.getProviderSubcontractor() != null
                            ? contractProviderSubcontractor.getProviderSubcontractor().getCorporateName()
                            : null)
                    .documentDtos(documentDtos)
                    .employeeDtos(employeeDtos)
                    .build();
        } else {
            throw new EntityNotFoundException("Contract not found");
        }
    }
}


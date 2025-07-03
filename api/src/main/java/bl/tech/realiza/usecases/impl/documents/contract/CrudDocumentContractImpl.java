package bl.tech.realiza.usecases.impl.documents.contract;

import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.responses.documents.ContractDocumentAndEmployeeResponseDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.services.auth.JwtService;
import bl.tech.realiza.usecases.interfaces.documents.contract.CrudDocumentContract;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CrudDocumentContractImpl implements CrudDocumentContract {
    private final ContractRepository contractRepository;
    private final JwtService jwtService;

    @Override
    public ContractDocumentAndEmployeeResponseDto getDocumentAndEmployeeByContractId(String id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found"));

        UserResponseDto requester = jwtService.extractAllClaims(jwtService.getTokenFromRequest());
        List<ContractDocumentAndEmployeeResponseDto.DocumentDto> documentDtos = new ArrayList<>();
        List<ContractDocumentAndEmployeeResponseDto.EmployeeDto> employeeDtos = new ArrayList<>();

        for (Employee employee : contract.getEmployees()) {
            employeeDtos.add(ContractDocumentAndEmployeeResponseDto.EmployeeDto.builder()
                    .id(employee.getIdEmployee())
                    .name(employee.getFullName())
                    .cboTitle(employee.getCbo() != null
                            ? employee.getCbo().getTitle() : null)
                    .build());

            for (DocumentEmployee documentEmployee : employee.getDocumentEmployees().stream()
                    .filter(doc -> doc.getContracts().contains(contract))
                    .toList()) {
                documentDtos.add(ContractDocumentAndEmployeeResponseDto.DocumentDto.builder()
                                .id(documentEmployee.getIdDocumentation())
                                .title(documentEmployee.getTitle())
                                .status(documentEmployee.getStatus())
                                .type(documentEmployee.getType())
                                .ownerName(documentEmployee.getEmployee().getFullName())
                                .hasDoc(documentEmployee.getIdDocumentation() != null
                                        && !documentEmployee.getIdDocumentation().isEmpty())
                                .enterprise(false)
                                .build());
            }
        }

        employeeDtos.sort(Comparator
                .comparing(ContractDocumentAndEmployeeResponseDto.EmployeeDto::getName, String.CASE_INSENSITIVE_ORDER));

        if (contract instanceof ContractProviderSupplier contractProviderSupplier) {
            if (contractProviderSupplier.getProviderSupplier() != null) {
                if (contractProviderSupplier.getProviderSupplier().getDocumentProviderSuppliers() != null) {
                    for (DocumentProviderSupplier documentProviderSupplier : contractProviderSupplier.getProviderSupplier().getDocumentProviderSuppliers().stream()
                            .filter(doc -> doc.getDocumentMatrix().getSubGroup().getGroup().getGroupName().equals("Documento empresa") ||
                                            doc.getDocumentMatrix().getSubGroup().getGroup().getGroupName().equals("Documentos empresa-serviço")).toList()) {
                        documentDtos.add(ContractDocumentAndEmployeeResponseDto.DocumentDto.builder()
                                .id(documentProviderSupplier.getIdDocumentation())
                                .title(documentProviderSupplier.getTitle())
                                .status(documentProviderSupplier.getStatus())
                                .type(documentProviderSupplier.getType())
                                .ownerName(documentProviderSupplier.getProviderSupplier() != null
                                        ? documentProviderSupplier.getProviderSupplier().getCorporateName()
                                        : null)
                                .hasDoc(documentProviderSupplier.getIdDocumentation() != null
                                        && !documentProviderSupplier.getIdDocumentation().isEmpty())
                                .enterprise(true)
                                .build());
                    }
                }
            }

            documentDtos.sort(Comparator
                    .comparing(ContractDocumentAndEmployeeResponseDto.DocumentDto::getEnterprise).reversed()
                    .thenComparing(ContractDocumentAndEmployeeResponseDto.DocumentDto::getTitle, String.CASE_INSENSITIVE_ORDER));


            if ((requester.getAdmin() != null ? requester.getAdmin() : false)
                    || requester.getRole().equals(User.Role.ROLE_REALIZA_BASIC)
                    || requester.getRole().equals(User.Role.ROLE_REALIZA_PLUS)) {
                return ContractDocumentAndEmployeeResponseDto.builder()
                        .enterpriseName(contractProviderSupplier.getProviderSupplier() != null
                                ? contractProviderSupplier.getProviderSupplier().getCorporateName()
                                : null)
                        .documentDtos(documentDtos)
                        .employeeDtos(employeeDtos)
                        .build();
            } else {
                if (!requester.getLaboral()) {
                    documentDtos.removeIf(documentDto -> documentDto.getType().equals("trabalhista"));
                }
                if (!requester.getWorkplaceSafety()) {
                    documentDtos.removeIf(documentDto -> documentDto.getType().equals("segurança do trabalho"));
                }
                if (!requester.getRegistrationAndCertificates()) {
                    documentDtos.removeIf(documentDto -> documentDto.getType().equals("cadastro e certidões"));
                }
                if (!requester.getGeneral()) {
                    documentDtos.removeIf(documentDto -> documentDto.getType().equals("geral"));
                }
                if (!requester.getHealth()) {
                    documentDtos.removeIf(documentDto -> documentDto.getType().equals("saude"));
                }
                if (!requester.getEnvironment()) {
                    documentDtos.removeIf(documentDto -> documentDto.getType().equals("meio ambiente"));
                }

                return ContractDocumentAndEmployeeResponseDto.builder()
                        .enterpriseName(contractProviderSupplier.getProviderSupplier() != null
                                ? contractProviderSupplier.getProviderSupplier().getCorporateName()
                                : null)
                        .documentDtos(documentDtos)
                        .employeeDtos(employeeDtos)
                        .build();
            }

        } else if (contract instanceof ContractProviderSubcontractor contractProviderSubcontractor) {
            if (contractProviderSubcontractor.getProviderSubcontractor() != null) {
                if (contractProviderSubcontractor.getProviderSubcontractor().getDocumentProviderSubcontractors() != null) {
                    for (DocumentProviderSubcontractor documentProviderSubcontractor : contractProviderSubcontractor.getProviderSubcontractor().getDocumentProviderSubcontractors().stream()
                            .filter(doc -> doc.getDocumentMatrix().getSubGroup().getGroup().getGroupName().equals("Documento empresa") ||
                                    doc.getDocumentMatrix().getSubGroup().getGroup().getGroupName().equals("Documentos empresa-serviço")).toList()) {
                        documentDtos.add(ContractDocumentAndEmployeeResponseDto.DocumentDto.builder()
                                .id(documentProviderSubcontractor.getIdDocumentation())
                                .title(documentProviderSubcontractor.getTitle())
                                .status(documentProviderSubcontractor.getStatus())
                                .type(documentProviderSubcontractor.getType())
                                .ownerName(documentProviderSubcontractor.getProviderSubcontractor() != null
                                        ? documentProviderSubcontractor.getProviderSubcontractor().getCorporateName()
                                        : null)
                                .hasDoc(documentProviderSubcontractor.getIdDocumentation() != null
                                        && !documentProviderSubcontractor.getIdDocumentation().isEmpty())
                                .enterprise(true)
                                .build());
                    }
                }
            }

            if ((requester.getAdmin() != null ? requester.getAdmin() : false)
                    || requester.getRole().equals(User.Role.ROLE_REALIZA_BASIC)
                    || requester.getRole().equals(User.Role.ROLE_REALIZA_PLUS)) {
                return ContractDocumentAndEmployeeResponseDto.builder()
                        .enterpriseName(contractProviderSubcontractor.getProviderSubcontractor() != null
                                ? contractProviderSubcontractor.getProviderSubcontractor().getCorporateName()
                                : null)
                        .documentDtos(documentDtos)
                        .employeeDtos(employeeDtos)
                        .build();
            } else {
                if (!requester.getLaboral()) {
                    documentDtos.removeIf(documentDto -> documentDto.getType().equals("trabalhista"));
                }
                if (!requester.getWorkplaceSafety()) {
                    documentDtos.removeIf(documentDto -> documentDto.getType().equals("segurança do trabalho"));
                }
                if (!requester.getRegistrationAndCertificates()) {
                    documentDtos.removeIf(documentDto -> documentDto.getType().equals("cadastro e certidões"));
                }
                if (!requester.getGeneral()) {
                    documentDtos.removeIf(documentDto -> documentDto.getType().equals("geral"));
                }
                if (!requester.getHealth()) {
                    documentDtos.removeIf(documentDto -> documentDto.getType().equals("saude"));
                }
                if (!requester.getEnvironment()) {
                    documentDtos.removeIf(documentDto -> documentDto.getType().equals("meio ambiente"));
                }

                return ContractDocumentAndEmployeeResponseDto.builder()
                        .enterpriseName(contractProviderSubcontractor.getProviderSubcontractor() != null
                                ? contractProviderSubcontractor.getProviderSubcontractor().getCorporateName()
                                : null)
                        .documentDtos(documentDtos)
                        .employeeDtos(employeeDtos)
                        .build();
            }

        } else {
            throw new EntityNotFoundException("Contract not found");
        }
    }
}


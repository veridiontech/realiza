package bl.tech.realiza.usecases.impl.documents.contract;

import bl.tech.realiza.domains.contract.ContractEmployee;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.gateways.repositories.contracts.ContractEmployeeRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.documents.employee.DocumentEmployeeRepository;
import bl.tech.realiza.gateways.responses.documents.ContractDocumentAndEmployeeResponseDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.services.auth.JwtService;
import bl.tech.realiza.usecases.interfaces.documents.contract.CrudDocumentContract;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CrudDocumentContractImpl implements CrudDocumentContract {
    private final ContractRepository contractRepository;
    private final JwtService jwtService;
    private final DocumentEmployeeRepository documentEmployeeRepository;
    private final ContractEmployeeRepository contractEmployeeRepository;

    @Override
    public ContractDocumentAndEmployeeResponseDto getDocumentAndEmployeeByContractId(String id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found"));

        UserResponseDto requester = jwtService.extractAllClaims(jwtService.getTokenFromRequest());
        List<ContractDocumentAndEmployeeResponseDto.DocumentDto> documentDtos = new ArrayList<>();
        List<ContractDocumentAndEmployeeResponseDto.DocumentDto> employeeDocuments = new ArrayList<>();
        List<ContractDocumentAndEmployeeResponseDto.EmployeeDto> employeeDtos = new ArrayList<>();

        for (ContractEmployee contractEmployee1 : contract.getEmployeeContracts()) {
            Boolean documentsConformity;
            List<DocumentEmployee> documentsEmployee = documentEmployeeRepository.findAllByEmployee_IdEmployeeAndContractDocuments_Contract_IdContractAndConformingAndDoesBlock(
                    contractEmployee1.getEmployee().getIdEmployee(),
                    contract.getIdContract(),
                    false,
                    true);
            documentsConformity = documentsEmployee.isEmpty();
            ContractEmployee contractEmployee = contractEmployeeRepository.findByContract_IdContractAndEmployee_IdEmployee(id,contractEmployee1.getEmployee().getIdEmployee())
                            .orElseThrow(() -> new EntityNotFoundException("Contract-Employee not found"));
            employeeDtos.add(ContractDocumentAndEmployeeResponseDto.EmployeeDto.builder()
                    .id(contractEmployee1.getEmployee().getIdEmployee())
                    .name(contractEmployee1.getEmployee().getFullName())
                    .cboTitle(contractEmployee1.getEmployee().getCbo() != null
                            ? contractEmployee1.getEmployee().getCbo().getTitle() : null)
                    .documentsConformity(documentsConformity)
                    .integrated(contractEmployee.getIntegrated())
                    .build());
            List<DocumentEmployee> documentEmployeeList = contractEmployee1.getEmployee().getDocumentEmployees().stream()
                    .filter(documentEmployee -> documentEmployee.getContractDocuments().stream()
                            .anyMatch(contractDocument -> contractDocument.getContract().equals(contract)))
                    .toList();
//            List<DocumentEmployee> documentEmployeeList = documentEmployeeRepository
//                    .findAllByEmployee_IdEmployeeAndContractDocuments_Contract_IdContract(contractEmployee1.getIdEmployee(),
//                            contract.getIdContract());

            for (DocumentEmployee documentEmployee : documentEmployeeList) {
                employeeDocuments.add(ContractDocumentAndEmployeeResponseDto.DocumentDto.builder()
                                .id(documentEmployee.getIdDocumentation())
                                .title(documentEmployee.getTitle())
                                .status(documentEmployee.getStatus())
                                .type(documentEmployee.getType())
                                .ownerName(documentEmployee.getEmployee().getFullName())
                                .isUnique(documentEmployee.getDocumentMatrix().getIsDocumentUnique())
                                .hasDoc(documentEmployee.getIdDocumentation() != null
                                        && !documentEmployee.getIdDocumentation().isEmpty())
                                .expirationDate(documentEmployee.getExpirationDate())
                                .uploadDate(documentEmployee.getVersionDate())
                                .lastCheck(documentEmployee.getLastCheck())
                                .enterprise(false)
                                .build());
            }
        }

        employeeDtos.sort(Comparator
                .comparing(ContractDocumentAndEmployeeResponseDto.EmployeeDto::getName, String.CASE_INSENSITIVE_ORDER));

        if (contract instanceof ContractProviderSupplier contractProviderSupplier) {
            if (contractProviderSupplier.getProviderSupplier() != null && contractProviderSupplier.getProviderSupplier().getDocumentProviderSuppliers() != null) {

                // Lista dos nomes de grupo que você quer encontrar
                List<String> targetGroups = List.of("Documento empresa", "Documentos empresa-serviço");

                // 1. Filtre a lista de documentos com segurança
                List<DocumentProviderSupplier> filteredDocuments = contractProviderSupplier.getProviderSupplier().getDocumentProviderSuppliers().stream()
                        .filter(doc -> {
                            // Verificação de segurança contra nulos
                            if (doc.getDocumentMatrix() == null || doc.getDocumentMatrix().getGroup() == null || doc.getDocumentMatrix().getGroup().getGroupName() == null) {
                                return false; // Ignora documentos com matriz, grupo ou nome de grupo nulos
                            }
                            // Retorna true se o nome do grupo estiver na nossa lista de alvos
                            return targetGroups.contains(doc.getDocumentMatrix().getGroup().getGroupName());
                        })
                        .toList(); // 2. Converte o resultado do stream para uma lista

                // 3. Agora, itere sobre a lista JÁ FILTRADA
                for (DocumentProviderSupplier documentProviderSupplier : filteredDocuments) {
                    documentDtos.add(ContractDocumentAndEmployeeResponseDto.DocumentDto.builder()
                            .id(documentProviderSupplier.getIdDocumentation())
                            .title(documentProviderSupplier.getTitle())
                            .status(documentProviderSupplier.getStatus())
                            .type(documentProviderSupplier.getType())
                            .ownerName(documentProviderSupplier.getProviderSupplier() != null
                                    ? documentProviderSupplier.getProviderSupplier().getCorporateName()
                                    : null)
                            .isUnique(documentProviderSupplier.getDocumentMatrix().getIsDocumentUnique())
                            .hasDoc(documentProviderSupplier.getIdDocumentation() != null
                                    && !documentProviderSupplier.getIdDocumentation().isEmpty())
                            .expirationDate(documentProviderSupplier.getExpirationDate())
                            .uploadDate(documentProviderSupplier.getVersionDate())
                            .lastCheck(documentProviderSupplier.getLastCheck())
                            .enterprise(true)
                            .build());
                }
            }

            documentDtos.sort(Comparator
                    .comparing(ContractDocumentAndEmployeeResponseDto.DocumentDto::getEnterprise).reversed()
                    .thenComparing(ContractDocumentAndEmployeeResponseDto.DocumentDto::getTitle, String.CASE_INSENSITIVE_ORDER));

            employeeDocuments.sort(Comparator
                    .comparing(ContractDocumentAndEmployeeResponseDto.DocumentDto::getEnterprise).reversed()
                    .thenComparing(ContractDocumentAndEmployeeResponseDto.DocumentDto::getTitle, String.CASE_INSENSITIVE_ORDER));


            if ((requester.getAdmin() != null ? requester.getAdmin() : false)
                    || requester.getRole().equals(User.Role.ROLE_REALIZA_BASIC)
                    || requester.getRole().equals(User.Role.ROLE_REALIZA_PLUS)) {

            } else {
                if (!requester.getLaboral()) {
                    documentDtos.removeIf(documentDto -> documentDto.getType().equals("trabalhista"));
                    employeeDocuments.removeIf(documentDto -> documentDto.getType().equals("trabalhista"));
                }
                if (!requester.getWorkplaceSafety()) {
                    documentDtos.removeIf(documentDto -> documentDto.getType().equals("segurança do trabalho"));
                    employeeDocuments.removeIf(documentDto -> documentDto.getType().equals("segurança do trabalho"));
                }
                if (!requester.getRegistrationAndCertificates()) {
                    documentDtos.removeIf(documentDto -> documentDto.getType().equals("cadastro e certidões"));
                    employeeDocuments.removeIf(documentDto -> documentDto.getType().equals("cadastro e certidões"));
                }
                if (!requester.getGeneral()) {
                    documentDtos.removeIf(documentDto -> documentDto.getType().equals("geral"));
                    employeeDocuments.removeIf(documentDto -> documentDto.getType().equals("geral"));
                }
                if (!requester.getHealth()) {
                    documentDtos.removeIf(documentDto -> documentDto.getType().equals("saude"));
                    employeeDocuments.removeIf(documentDto -> documentDto.getType().equals("saude"));
                }
                if (!requester.getEnvironment()) {
                    documentDtos.removeIf(documentDto -> documentDto.getType().equals("meio ambiente"));
                    employeeDocuments.removeIf(documentDto -> documentDto.getType().equals("meio ambiente"));
                }
            }
            return ContractDocumentAndEmployeeResponseDto.builder()
                    .enterpriseName(contractProviderSupplier.getProviderSupplier() != null
                            ? contractProviderSupplier.getProviderSupplier().getCorporateName()
                            : null)
                    .documentDtos(documentDtos)
                    .employeeDocuments(employeeDocuments)
                    .employeeDtos(employeeDtos)
                    .build();

        } else if (contract instanceof ContractProviderSubcontractor contractProviderSubcontractor) {
            if (contractProviderSubcontractor.getProviderSubcontractor() != null) {
                if (contractProviderSubcontractor.getProviderSubcontractor().getDocumentProviderSubcontractors() != null) {
                    for (DocumentProviderSubcontractor documentProviderSubcontractor : contractProviderSubcontractor.getProviderSubcontractor().getDocumentProviderSubcontractors().stream()
                            .filter(doc -> doc.getDocumentMatrix().getGroup().getGroupName().equals("Documento empresa") ||
                                    doc.getDocumentMatrix().getGroup().getGroupName().equals("Documentos empresa-serviço")).toList()) {
                        documentDtos.add(ContractDocumentAndEmployeeResponseDto.DocumentDto.builder()
                                .id(documentProviderSubcontractor.getIdDocumentation())
                                .title(documentProviderSubcontractor.getTitle())
                                .status(documentProviderSubcontractor.getStatus())
                                .type(documentProviderSubcontractor.getType())
                                .ownerName(documentProviderSubcontractor.getProviderSubcontractor() != null
                                        ? documentProviderSubcontractor.getProviderSubcontractor().getCorporateName()
                                        : null)
                                .isUnique(documentProviderSubcontractor.getDocumentMatrix().getIsDocumentUnique())
                                .hasDoc(documentProviderSubcontractor.getIdDocumentation() != null
                                        && !documentProviderSubcontractor.getIdDocumentation().isEmpty())
                                .expirationDate(documentProviderSubcontractor.getExpirationDate())
                                .uploadDate(documentProviderSubcontractor.getVersionDate())
                                .lastCheck(documentProviderSubcontractor.getLastCheck())
                                .enterprise(true)
                                .build());
                    }
                }
            }

            if ((requester.getAdmin() != null ? requester.getAdmin() : false)
                    || requester.getRole().equals(User.Role.ROLE_REALIZA_BASIC)
                    || requester.getRole().equals(User.Role.ROLE_REALIZA_PLUS)) {

            } else {
                if (!requester.getLaboral()) {
                    documentDtos.removeIf(documentDto -> documentDto.getType().equals("trabalhista"));
                    employeeDocuments.removeIf(documentDto -> documentDto.getType().equals("trabalhista"));
                }
                if (!requester.getWorkplaceSafety()) {
                    documentDtos.removeIf(documentDto -> documentDto.getType().equals("segurança do trabalho"));
                    employeeDocuments.removeIf(documentDto -> documentDto.getType().equals("segurança do trabalho"));
                }
                if (!requester.getRegistrationAndCertificates()) {
                    documentDtos.removeIf(documentDto -> documentDto.getType().equals("cadastro e certidões"));
                    employeeDocuments.removeIf(documentDto -> documentDto.getType().equals("cadastro e certidões"));
                }
                if (!requester.getGeneral()) {
                    documentDtos.removeIf(documentDto -> documentDto.getType().equals("geral"));
                    employeeDocuments.removeIf(documentDto -> documentDto.getType().equals("geral"));
                }
                if (!requester.getHealth()) {
                    documentDtos.removeIf(documentDto -> documentDto.getType().equals("saude"));
                    employeeDocuments.removeIf(documentDto -> documentDto.getType().equals("saude"));
                }
                if (!requester.getEnvironment()) {
                    documentDtos.removeIf(documentDto -> documentDto.getType().equals("meio ambiente"));
                    employeeDocuments.removeIf(documentDto -> documentDto.getType().equals("meio ambiente"));
                }
            }
            return ContractDocumentAndEmployeeResponseDto.builder()
                    .enterpriseName(contractProviderSubcontractor.getProviderSubcontractor() != null
                            ? contractProviderSubcontractor.getProviderSubcontractor().getCorporateName()
                            : null)
                    .documentDtos(documentDtos)
                    .employeeDocuments(employeeDocuments)
                    .employeeDtos(employeeDtos)
                    .build();
        } else {
            throw new EntityNotFoundException("Contract not found");
        }
    }
}


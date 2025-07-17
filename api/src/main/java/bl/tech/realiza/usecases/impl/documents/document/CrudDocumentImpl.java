package bl.tech.realiza.usecases.impl.documents.document;

import bl.tech.realiza.domains.auditLogs.document.AuditLogDocument;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.ContractDocument;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.enums.AuditLogActionsEnum;
import bl.tech.realiza.domains.enums.AuditLogTypeEnum;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.auditLogs.document.AuditLogDocumentRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.documents.DocumentRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentBranchRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.documents.DocumentStatusChangeRequestDto;
import bl.tech.realiza.services.auth.JwtService;
import bl.tech.realiza.usecases.interfaces.auditLogs.AuditLogService;
import bl.tech.realiza.usecases.interfaces.documents.document.CrudDocument;
import bl.tech.realiza.usecases.interfaces.users.CrudNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static bl.tech.realiza.domains.documents.Document.Status.*;
import static bl.tech.realiza.domains.enums.AuditLogActionsEnum.*;
import static bl.tech.realiza.domains.enums.AuditLogActionsEnum.APPROVE;
import static bl.tech.realiza.domains.enums.AuditLogActionsEnum.REJECT;
import static bl.tech.realiza.domains.enums.AuditLogTypeEnum.*;

@Service
@RequiredArgsConstructor
public class CrudDocumentImpl implements CrudDocument {

    private final DocumentRepository documentRepository;
    private final CrudNotification crudNotification;
    private final UserRepository userRepository;
    private final ContractRepository contractRepository;
    private final AuditLogService auditLogServiceImpl;
    private final DocumentBranchRepository documentBranchRepository;

    @Override
    public void expirationChange() {
        int page = 0;
        int size = 50;
        boolean hasNext;

        Page<Document> documentPage;
        do {
            documentPage = documentRepository.findAllByStatus(
                    APROVADO, PageRequest.of(page, size)
            );

            documentPage.forEach(document -> {
                if (document.getExpirationDate() != null &&
                        document.getExpirationDate().isBefore(LocalDate.now().atStartOfDay())) {
                    document.setStatus(VENCIDO);
                    document.setConforming(false);
                    documentRepository.save(document);
                }
            });

            hasNext = documentPage.hasNext();
            page++;
        } while (hasNext);
    }


    @Override
    public void expirationCheck() {
        int page = 0;
        int size = 50;
        boolean hasNext;

        Page<Document> documentPage;
        do {
            documentPage = documentRepository.findAllByStatus(
                    VENCIDO, PageRequest.of(page, size)
            );

            documentPage.forEach(document -> {
                if (document instanceof DocumentProviderSupplier providerSupplierDoc) {
                    crudNotification.saveExpiredSupplierDocumentNotificationForSupplierUsers(providerSupplierDoc);
                } else if (document instanceof DocumentProviderSubcontractor providerSubcontractorDoc) {
                    crudNotification.saveExpiredSubcontractDocumentNotificationForSubcontractorUsers(providerSubcontractorDoc);
                } else if (document instanceof DocumentEmployee employeeDoc) {
                    crudNotification.saveExpiredEmployeeDocumentNotificationForManagerUsers(employeeDoc);
                }
            });

            hasNext = documentPage.hasNext();
            page++;
        } while (hasNext);
    }

    @Override
    public String changeStatus(String documentId, DocumentStatusChangeRequestDto documentStatusChangeRequestDto) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Document not found"));
        document.setStatus(documentStatusChangeRequestDto.getStatus());
        for (ContractDocument contractDocument : document.getContractDocuments()) {
            contractDocument.setStatus(contractDocument.getStatus());
        }

        if (document.getStatus() == APROVADO) {
            document.setConforming(true);
            DocumentMatrix.Unit expirationUnit = null;
            Integer expirationAmount = 0;
            String documentMatrixId = document.getDocumentMatrix().getIdDocument();
            String branchId = null;
            if (document instanceof DocumentProviderSupplier documentProviderSupplier) {
                branchId = documentProviderSupplier.getProviderSupplier()
                                .getBranches().get(documentProviderSupplier.getProviderSupplier().getBranches().size() - 1)
                                .getIdBranch();
            } else if (document instanceof DocumentProviderSubcontractor documentProviderSubcontractor) {
                branchId = documentProviderSubcontractor.getProviderSubcontractor().getProviderSupplier()
                        .getBranches().get(documentProviderSubcontractor.getProviderSubcontractor().getProviderSupplier().getBranches().size() - 1)
                        .getIdBranch();
            } else if (document instanceof DocumentEmployee documentEmployee) {
                if (documentEmployee.getEmployee().getSupplier() != null) {
                    branchId = documentEmployee.getEmployee().getSupplier()
                            .getBranches().get(documentEmployee.getEmployee().getSupplier().getBranches().size() - 1)
                            .getIdBranch();
                } else if (documentEmployee.getEmployee().getSubcontract() != null) {
                    branchId = documentEmployee.getEmployee().getSubcontract().getProviderSupplier()
                            .getBranches().get(documentEmployee.getEmployee().getSubcontract().getProviderSupplier().getBranches().size() - 1)
                            .getIdBranch();
                }
            }
            List<DocumentBranch> documentBranches = documentBranchRepository.findAllByBranch_IdBranchAndDocumentMatrix_IdDocument(branchId,documentMatrixId);
            expirationUnit = documentBranches.get(documentBranches.size() - 1).getExpirationDateUnit();
            expirationAmount = documentBranches.get(documentBranches.size() - 1).getExpirationDateAmount();
            LocalDateTime documentDate = document.getDocumentDate() != null
                    ? document.getDocumentDate()
                    : LocalDateTime.now();
            if (expirationAmount == 0) {
                document.setExpirationDate(document.getDocumentDate()
                        .plusYears(100));
            } else {
                switch (expirationUnit) {
                    case DAYS -> document.setExpirationDate(documentDate
                            .plusDays(expirationAmount));
                    case WEEKS -> document.setExpirationDate(documentDate
                            .plusWeeks(expirationAmount));
                    case MONTHS -> document.setExpirationDate(documentDate
                            .plusMonths(expirationAmount));
                    case YEARS -> document.setExpirationDate(documentDate
                            .plusYears(expirationAmount));
                }
            }
        } else {
            document.setConforming(false);
        }
        document.setLastCheck(LocalDateTime.now());
        documentRepository.save(document);

        AuditLogActionsEnum action;
        switch (documentStatusChangeRequestDto.getStatus()) {
            case REPROVADO -> action = REJECT;
            case APROVADO -> action = APPROVE;
            default -> throw new IllegalStateException("Status change not valid for status " + documentStatusChangeRequestDto.getStatus());
        }
        User user = userRepository.findById(Objects.requireNonNull(JwtService.getAuthenticatedUserId()))
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            if (userResponsible != null) {
                auditLogServiceImpl.createAuditLog(
                        document.getIdDocumentation(),
                        DOCUMENT,
                        user.getFullName() + " " + action.name()
                                + " document " + document.getTitle(),
                        ChronoUnit.DAYS.between(document.getVersionDate(), LocalDateTime.now()) + " dias entre o upload e a validação",
                        action,
                        userResponsible.getIdUser());
            }
        }

        return "Document status changed to " + documentStatusChangeRequestDto.getStatus().name();
    }

    @Override
    public String documentExemption(String documentId, String contractId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Document not found"));

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        if (document.getContractDocuments().stream().anyMatch(contractDocument -> contractDocument.getContract().equals(contract))) {
            document.getContractDocuments().removeIf(contractDocument -> contractDocument.getContract().equals(contract));

            if (document.getContractDocuments().isEmpty()) {
                documentRepository.delete(document);
            } else {
                documentRepository.save(document);
            }
            contractRepository.save(contract);
        }

        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            String owner = "";
            if (userResponsible != null) {
                if (document instanceof DocumentEmployee documentEmployee) {
                    owner = documentEmployee.getEmployee() != null
                            ? documentEmployee.getEmployee().getName()
                            + (documentEmployee.getEmployee().getSurname() != null ?
                            " " + documentEmployee.getEmployee().getSurname() : "")
                            : "Not Identified";
                } else if (document instanceof DocumentProviderSupplier documentProviderSupplier) {
                    owner = documentProviderSupplier.getProviderSupplier() != null
                            ? (documentProviderSupplier.getProviderSupplier().getCorporateName() != null
                                ? documentProviderSupplier.getProviderSupplier().getCorporateName()
                                : (documentProviderSupplier.getProviderSupplier().getTradeName() != null
                                    ? documentProviderSupplier.getProviderSupplier().getTradeName()
                                    : "Not Identified"))
                            : "Not Identified";
                } else if (document instanceof DocumentProviderSubcontractor documentProviderSubcontractor) {
                    owner = documentProviderSubcontractor.getProviderSubcontractor() != null
                            ? (documentProviderSubcontractor.getProviderSubcontractor().getCorporateName() != null
                            ? documentProviderSubcontractor.getProviderSubcontractor().getCorporateName()
                            : (documentProviderSubcontractor.getProviderSubcontractor().getTradeName() != null
                            ? documentProviderSubcontractor.getProviderSubcontractor().getTradeName()
                            : "Not Identified"))
                            : "Not Identified";
                }
                auditLogServiceImpl.createAuditLog(
                        document.getIdDocumentation(),
                        DOCUMENT,
                        userResponsible.getFullName() + " isentou documento "
                                + document.getTitle() + " de " + owner,
                        null,
                        EXEMPT,
                        userResponsible.getIdUser());
            }
        }

        return "Document " + document.getTitle() + " exempted from contract " + contract.getContractReference();
    }
}

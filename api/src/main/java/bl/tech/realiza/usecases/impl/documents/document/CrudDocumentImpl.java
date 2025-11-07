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
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import bl.tech.realiza.domains.enums.DocumentStatusEnum;
import bl.tech.realiza.domains.enums.DocumentValidityEnum;
import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.domains.services.ItemManagement;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.auditLogs.document.AuditLogDocumentRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractDocumentRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.documents.DocumentRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentBranchRepository;
import bl.tech.realiza.gateways.repositories.documents.employee.DocumentEmployeeRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.documents.DocumentStatusChangeRequestDto;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementDocumentRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentPendingResponseDto;
import bl.tech.realiza.services.GoogleCloudService;
import bl.tech.realiza.services.auth.JwtService;
import bl.tech.realiza.usecases.impl.CrudItemManagementImpl;
import bl.tech.realiza.usecases.interfaces.auditLogs.AuditLogService;
import bl.tech.realiza.usecases.interfaces.documents.document.CrudDocument;
import bl.tech.realiza.usecases.interfaces.users.CrudNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static bl.tech.realiza.domains.documents.Document.Status.*;
import static bl.tech.realiza.domains.enums.AuditLogActionsEnum.*;
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
    private final ProviderRepository providerRepository;
    private final DocumentProviderSupplierRepository documentProviderSupplierRepository;
    private final GoogleCloudService googleCloudService;
    private final DocumentEmployeeRepository documentEmployeeRepository;
    private final DocumentProviderSubcontractorRepository documentProviderSubcontractorRepository;
    private final ContractDocumentRepository contractDocumentRepository;
    private final CrudItemManagementImpl crudItemManagementImpl;
    private final AuditLogDocumentRepository auditLogDocumentRepository;
    private final FileRepository fileRepository;

    @Override
    public void expirationChange() {
        int page = 0;
        int size = 50;
        boolean hasNext;

        Page<Document> documentPage;
        List<ContractStatusEnum> statusEnumList = new ArrayList<>();
        statusEnumList.add(ContractStatusEnum.FINISHED);
        statusEnumList.add(ContractStatusEnum.SUSPENDED);
        statusEnumList.add(ContractStatusEnum.DENIED);
        do {
            documentPage = documentRepository.findAllByStatusAndNotInContractStatuses(
                    APROVADO, statusEnumList, PageRequest.of(page, size)
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
    public void deleteReprovedCheck() {
        Pageable pageable = PageRequest.of(0, 50);
        Page<Document> documents = documentRepository.findAllByStatusAndLastCheckAfter(APROVADO, LocalDateTime.now().minusHours(27), pageable);
        while (documents.hasContent()) {
            for (Document document : documents) {
                List<AuditLogDocument> auditLogDocuments = auditLogDocumentRepository.findAllByDocumentId(document.getIdDocumentation());
                for (AuditLogDocument auditLogDocument : auditLogDocuments) {
                    if (ChronoUnit.MONTHS.between(auditLogDocument.getCreatedAt(), LocalDateTime.now()) > 2) {
                        try {
                            googleCloudService.deleteFile(document.getFileName());
                            documentRepository.delete(document);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            pageable = pageable.next();
            documents = documentRepository.findAllByStatusAndLastCheckAfter(APROVADO, LocalDateTime.now().minusHours(27), pageable);
        }
    }

    @Override
    @Transactional
    public String changeStatus(String documentId, DocumentStatusChangeRequestDto documentStatusChangeRequestDto) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Document not found"));

        document.setStatus(documentStatusChangeRequestDto.getStatus());
        document.setJustification(documentStatusChangeRequestDto.getJustification());

        if (documentStatusChangeRequestDto.getStatus().equals(APROVADO)) {
            Integer expirationAmount = document.getExpirationAmount();
            Document.ExpirationDayUnitEnum expirationDayUnitEnum = document.getExpirationDayUnitEnum();
            LocalDateTime documentDate = document.getDocumentDate() != null ? document.getDocumentDate() : LocalDateTime.now();

            if (expirationAmount == 0 || !document.getValidity().equals(DocumentValidityEnum.INDEFINITE)) {
                document.setExpirationDate(document.getDocumentDate()
                        .plusYears(100));
            } else {
                switch (expirationDayUnitEnum) {
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

        if (document instanceof DocumentBranch) {
            if (documentStatusChangeRequestDto.getBranchIds() != null && !documentStatusChangeRequestDto.getBranchIds().isEmpty()) {
                List<DocumentBranch> documentBranches = documentBranchRepository.findAllById(documentStatusChangeRequestDto.getBranchIds());
                documentBranches.forEach(db -> {
                    db.setStatus(documentStatusChangeRequestDto.getStatus());
                    db.setJustification(documentStatusChangeRequestDto.getJustification());
                    db.setLastCheck(LocalDateTime.now());
                    documentBranchRepository.save(db);
                });
            }
        }

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
                        documentStatusChangeRequestDto.getJustification(),
                        ChronoUnit.DAYS.between(document.getVersionDate(), LocalDateTime.now()) + " dias entre o upload e a validação",
                        action,
                        userResponsible.getIdUser());
            }
        }

        return "Document status changed to " + documentStatusChangeRequestDto.getStatus().name();
    }

    @Override
    @Transactional
    public String changeDocumentBranchStatus(String documentBranchId, DocumentStatusChangeRequestDto documentStatusChangeRequestDto) {
        DocumentBranch documentBranch = documentBranchRepository.findById(documentBranchId)
                .orElseThrow(() -> new NotFoundException("DocumentBranch not found"));

        Document document = documentRepository.findTopByDocumentMatrixAndVersionDateBeforeOrderByVersionDateDesc(
                documentBranch.getDocumentMatrix(),
                documentBranch.getVersionDate()
        ).orElseThrow(() -> new NotFoundException("Document not found for DocumentBranch ID"));

        document.setStatus(documentStatusChangeRequestDto.getStatus());
        document.setJustification(documentStatusChangeRequestDto.getJustification());

        if (documentStatusChangeRequestDto.getStatus().equals(APROVADO)) {
            Integer expirationAmount = document.getExpirationAmount();
            Document.ExpirationDayUnitEnum expirationDayUnitEnum = document.getExpirationDayUnitEnum();
            LocalDateTime documentDate = document.getDocumentDate() != null ? document.getDocumentDate() : LocalDateTime.now();

            if (expirationAmount == 0 || !document.getValidity().equals(DocumentValidityEnum.INDEFINITE)) {
                document.setExpirationDate(document.getDocumentDate()
                        .plusYears(100));
            } else {
                switch (expirationDayUnitEnum) {
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
                        documentStatusChangeRequestDto.getJustification(),
                        ChronoUnit.DAYS.between(document.getVersionDate(), LocalDateTime.now()) + " dias entre o upload e a validação",
                        action,
                        userResponsible.getIdUser());
            }
        }

        return "Document status changed to " + documentStatusChangeRequestDto.getStatus().name();
    }

    @Override
    public String documentExemptionRequest(String documentId, String contractId, String description) {
        Document document = documentRepository.findById(documentId).orElse(null);

        if (document == null) {
            // Tenta resolver o ID como DocumentBranch
            DocumentBranch documentBranch = documentBranchRepository.findById(documentId).orElse(null);
            if (documentBranch != null) {
                document = documentBranch;
            }
        }

        if (document == null) {
            throw new NotFoundException("Document not found");
        }

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        document.setStatus(ISENCAO_PENDENTE);
        documentRepository.save(document);

        crudNotification.saveExemptionRequestNotificationForAnalystUsers(document, contract, description);

        return "Exemption request sent successfully";
    }

    @Override
    public List<DocumentPendingResponseDto> getPendingDocuments(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Document> pendingDocuments = new ArrayList<>();

        if (user.getProvider() != null) {
            Provider provider = user.getProvider();
            if (provider instanceof ProviderSupplier) {
                pendingDocuments.addAll(documentRepository.findAllByProviderAndStatus(provider.getIdProvider(), PENDENTE));
            } else if (provider instanceof ProviderSubcontractor) {
                pendingDocuments.addAll(documentRepository.findAllByProviderAndStatus(provider.getIdProvider(), PENDENTE));
            }
        }

        return pendingDocuments.stream()
                .map(doc -> new DocumentPendingResponseDto(doc.getIdDocumentation(), doc.getTitle()))
                .collect(Collectors.toList());
    }

    @Override
    public Page<Document> getAllDocumentsByStatus(DocumentStatusEnum status, Pageable pageable) {
        return documentRepository.findAllByStatus(status, pageable);
    }

    @Override
    public Page<Document> getAllDocumentsByFilter(String search, String status, Pageable pageable) {
        Document.Status docStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                docStatus = Document.Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Tratar status inválido, se necessário
            }
        }
        return documentRepository.findAllByFilter(search, docStatus, pageable);
    }

    @Override
    public void createDocumentFromItemManagement(ItemManagementDocumentRequestDto itemManagementDocumentRequestDto) {
        ItemManagement itemManagement = crudItemManagementImpl.getById(itemManagementDocumentRequestDto.getItemManagementId());

        DocumentMatrix documentMatrix = itemManagement.getDocumentMatrix();

        if (documentMatrix != null) {
            if (itemManagement.getProvider() instanceof ProviderSupplier) {
                DocumentProviderSupplier document = new DocumentProviderSupplier();
                document.setProviderSupplier((ProviderSupplier) itemManagement.getProvider());
                document.setDocumentMatrix(documentMatrix);
                document.setTitle(documentMatrix.getName());
                document.setRequired(documentMatrix.getRequired());
                document.setDoesBlock(documentMatrix.getDoesBlock());
                document.setValidity(documentMatrix.getValidity());
                document.setExpirationAmount(documentMatrix.getExpirationAmount());
                document.setExpirationDayUnitEnum(documentMatrix.getExpirationDayUnit());
                document.setVersionDate(LocalDateTime.now());

                FileDocument fileDocument = new FileDocument();
                fileDocument.setFileName(itemManagementDocumentRequestDto.getFileName());
                fileDocument.setOriginalFileName(itemManagementDocumentRequestDto.getOriginalFileName());
                fileDocument.setDocument(document);
                fileRepository.save(fileDocument);

                document.setDocument(Collections.singletonList(fileDocument));
                documentProviderSupplierRepository.save(document);
            } else if (itemManagement.getProvider() instanceof ProviderSubcontractor) {
                DocumentProviderSubcontractor document = new DocumentProviderSubcontractor();
                document.setProviderSubcontractor((ProviderSubcontractor) itemManagement.getProvider());
                document.setDocumentMatrix(documentMatrix);
                document.setTitle(documentMatrix.getName());
                document.setRequired(documentMatrix.getRequired());
                document.setDoesBlock(documentMatrix.getDoesBlock());
                document.setValidity(documentMatrix.getValidity());
                document.setExpirationAmount(documentMatrix.getExpirationAmount());
                document.setExpirationDayUnitEnum(documentMatrix.getExpirationDayUnit());
                document.setVersionDate(LocalDateTime.now());

                FileDocument fileDocument = new FileDocument();
                fileDocument.setFileName(itemManagementDocumentRequestDto.getFileName());
                fileDocument.setOriginalFileName(itemManagementDocumentRequestDto.getOriginalFileName());
                fileDocument.setDocument(document);
                fileRepository.save(fileDocument);

                document.setDocument(Collections.singletonList(fileDocument));
                documentProviderSubcontractorRepository.save(document);
            }
        }
    }
}

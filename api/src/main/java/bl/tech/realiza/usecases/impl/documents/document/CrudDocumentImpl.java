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
                    if (ChronoUnit.MONTHS.between(auditLogDocument.getCreatedAt(),LocalDateTime.now()) >= 1) {
                        auditLogDocument.setHasDoc(false);
                    }
                }
                auditLogDocumentRepository.saveAll(auditLogDocuments);
            }
            if (documents.hasNext()) {
                documents = documentRepository.findAllByStatusAndLastCheckAfter(APROVADO, LocalDateTime.now().minusDays(1), documents.nextPageable());
            } else {
                break;
            }
        }
    }

    @Override
    @Transactional
    public String changeStatus(String documentId, DocumentStatusChangeRequestDto documentStatusChangeRequestDto) {
        Document document = documentRepository.findById(documentId).orElse(null);

        if (document == null) {
            // Tenta resolver o ID como DocumentBranch
            DocumentBranch documentBranch = documentBranchRepository.findById(documentId).orElse(null);
            if (documentBranch != null) {
                // Se for um DocumentBranch, tenta encontrar o Document mais recente
                document = documentRepository.findTopByDocumentMatrixAndVersionDateBeforeOrderByVersionDateDesc(
                        documentBranch.getDocumentMatrix(),
                        documentBranch.getVersionDate()
                ).orElseThrow(() -> new NotFoundException("Document not found for DocumentBranch ID"));
            } else {
                throw new NotFoundException("Document or DocumentBranch not found with ID: " + documentId);
            }
        }
        document.setStatus(documentStatusChangeRequestDto.getStatus());
        for (ContractDocument contractDocument : document.getContractDocuments()) {
            contractDocument.setStatus(documentStatusChangeRequestDto.getStatus()); // Corrigido para usar o novo status
        }

        if (document.getStatus() == APROVADO) {
            document.setConforming(true);
            DocumentMatrix.DayUnitEnum expirationDayUnitEnum = null;
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
            if (documentBranches.isEmpty()) {
                throw new NotFoundException("Document branch not found");
            }
            expirationDayUnitEnum = documentBranches.get(documentBranches.size() - 1).getExpirationDateUnit();
            expirationAmount = documentBranches.get(documentBranches.size() - 1).getExpirationDateAmount();
            if (expirationAmount == null) {
                expirationAmount = document.getDocumentMatrix().getExpirationDateAmount();
            }
            LocalDateTime documentDate = document.getDocumentDate() != null
                    ? document.getDocumentDate()
                    : LocalDateTime.now();
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
                // Se for um DocumentBranch, tenta encontrar o Document mais recente
                document = documentRepository.findTopByDocumentMatrixAndVersionDateBeforeOrderByVersionDateDesc(
                        documentBranch.getDocumentMatrix(),
                        documentBranch.getVersionDate()
                ).orElseThrow(() -> new NotFoundException("Document not found for DocumentBranch ID"));
            } else {
                throw new NotFoundException("Document or DocumentBranch not found with ID: " + documentId);
            }
        }

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        ContractDocument contractDocumentInList = document.getContractDocuments().stream()
                .filter(cd -> cd.getContract().equals(contract))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Contract and Document link not found"));
        ContractDocument contractDocument = contractDocumentRepository.findById(contractDocumentInList.getId())
                .orElseThrow(() -> new NotFoundException("Contract and Document link not found"));

        contractDocument.setStatus(ISENCAO_PENDENTE);
        contractDocumentRepository.save(contractDocument);

        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            crudItemManagementImpl.saveDocumentSolicitation(ItemManagementDocumentRequestDto.builder()
                    .idRequester(userResponsible != null ? userResponsible.getIdUser() : null)
                    .solicitationType(ItemManagement.SolicitationType.EXEMPTION)
                    .description(description)
                    .documentId(contractDocument.getDocument().getIdDocumentation())
                    .contractId(contractDocument.getContract().getIdContract())
                    .build());
            if (userResponsible != null) {
                String owner = "";
                if (document instanceof DocumentEmployee documentEmployee) {
                    owner = documentEmployee.getEmployee() != null
                            ? documentEmployee.getEmployee().getFullName()
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
                        userResponsible.getFullName() + " solicitou isenção do documento "
                                + document.getTitle() + " de " + owner,
                        null,
                        null,
                        EXEMPT,
                        userResponsible.getIdUser());
            }
        }

        return "Solicitation created";
    }

    @Override
    public List<DocumentPendingResponseDto> findNonConformingDocumentByEnterpriseId(String enterpriseId) {
        List<DocumentPendingResponseDto> responseDto = new ArrayList<>();
        Provider provider = providerRepository.findById(enterpriseId)
                .orElseThrow(() -> new NotFoundException("Provider not found"));

        if (provider instanceof ProviderSupplier) {
            List<DocumentProviderSupplier> enterpriseDocuments = documentProviderSupplierRepository.findAllByProviderSupplier_IdProviderAndConformingIsFalse(provider.getIdProvider());
            for (DocumentProviderSupplier documentProviderSupplier : enterpriseDocuments) {
                String signedUrl = null;
                FileDocument fileDocument = documentProviderSupplier.getDocument().stream()
                        .max(Comparator.comparing(FileDocument::getCreationDate))
                        .orElse(null);
                if (fileDocument != null) {
                    if (fileDocument.getUrl() != null) {
                        signedUrl = googleCloudService.generateSignedUrl(fileDocument.getUrl(), 15);
                    }
                }

                List<Contract> contracts = documentProviderSupplier.getContractDocuments().stream()
                        .filter(contractDocument ->
                                        contractDocument.getStatus().equals(PENDENTE)
                                || contractDocument.getStatus().equals(REPROVADO)
                                || contractDocument.getStatus().equals(VENCIDO))
                        .map(ContractDocument::getContract)
                        .filter(contract -> contract.getStatus().equals(ContractStatusEnum.ACTIVE))
                        .toList();

                List<String> contractReferences = contracts.stream()
                        .map(Contract::getContractReference)
                        .toList();

                responseDto.add(DocumentPendingResponseDto.builder()
                        .id(documentProviderSupplier.getIdDocumentation())
                        .status(documentProviderSupplier.getStatus())
                        .title(documentProviderSupplier.getTitle())
                        .contractReferences(contractReferences)
                        .signedUrl(signedUrl)
                        .build());
            }
            List<DocumentEmployee> employeeDocuments = documentEmployeeRepository.findAllByEmployee_Supplier_IdProvider(provider.getIdProvider());
            for (DocumentEmployee documentEmployee : employeeDocuments) {
                String signedUrl = null;
                FileDocument fileDocument = documentEmployee.getDocument().stream()
                        .max(Comparator.comparing(FileDocument::getCreationDate))
                        .orElse(null);
                if (fileDocument != null) {
                    if (fileDocument.getUrl() != null) {
                        signedUrl = googleCloudService.generateSignedUrl(fileDocument.getUrl(), 15);
                    }
                }

                List<Contract> contracts = documentEmployee.getContractDocuments().stream()
                        .filter(contractDocument ->
                                contractDocument.getStatus().equals(PENDENTE)
                                        || contractDocument.getStatus().equals(REPROVADO)
                                        || contractDocument.getStatus().equals(VENCIDO))
                        .map(ContractDocument::getContract)
                        .filter(contract -> contract.getStatus().equals(ContractStatusEnum.ACTIVE))
                        .toList();

                List<String> contractReferences = contracts.stream()
                        .map(Contract::getContractReference)
                        .toList();

                responseDto.add(DocumentPendingResponseDto.builder()
                        .id(documentEmployee.getIdDocumentation())
                        .status(documentEmployee.getStatus())
                        .title(documentEmployee.getTitle())
                        .owner(documentEmployee.getEmployee().getFullName())
                        .contractReferences(contractReferences)
                        .signedUrl(signedUrl)
                        .build());
            }
        } else if (provider instanceof ProviderSubcontractor) {
            List<DocumentProviderSubcontractor> enterpriseDocuments = documentProviderSubcontractorRepository.findAllByProviderSubcontractor_IdProviderAndConformingIsFalse(provider.getIdProvider());
            for (DocumentProviderSubcontractor documentProviderSubcontractor : enterpriseDocuments) {
                String signedUrl = null;
                FileDocument fileDocument = documentProviderSubcontractor.getDocument().stream()
                        .max(Comparator.comparing(FileDocument::getCreationDate))
                        .orElse(null);
                if (fileDocument != null) {
                    if (fileDocument.getUrl() != null) {
                        signedUrl = googleCloudService.generateSignedUrl(fileDocument.getUrl(), 15);
                    }
                }

                responseDto.add(DocumentPendingResponseDto.builder()
                        .id(documentProviderSubcontractor.getIdDocumentation())
                        .status(documentProviderSubcontractor.getStatus())
                        .title(documentProviderSubcontractor.getTitle())
                        .owner(documentProviderSubcontractor.getProviderSubcontractor().getCorporateName())
                        .signedUrl(signedUrl)
                        .build());
            }
            List<DocumentEmployee> employeeDocuments = documentEmployeeRepository.findAllByEmployee_Subcontract_IdProvider(provider.getIdProvider());
            for (DocumentEmployee documentEmployee : employeeDocuments) {
                String signedUrl = null;
                FileDocument fileDocument = documentEmployee.getDocument().stream()
                        .max(Comparator.comparing(FileDocument::getCreationDate))
                        .orElse(null);
                if (fileDocument != null) {
                    if (fileDocument.getUrl() != null) {
                        signedUrl = googleCloudService.generateSignedUrl(fileDocument.getUrl(), 15);
                    }
                }

                responseDto.add(DocumentPendingResponseDto.builder()
                        .id(documentEmployee.getIdDocumentation())
                        .status(documentEmployee.getStatus())
                        .title(documentEmployee.getTitle())
                        .owner(documentEmployee.getEmployee().getFullName())
                        .signedUrl(signedUrl)
                        .build());
            }
        }
        return responseDto;
    }

    @Override
    public String findVersionByAuditLog(String auditLogId) {
        AuditLogDocument auditLog = auditLogDocumentRepository.findById(auditLogId)
                .orElseThrow(() -> new NotFoundException("Audit Log not found"));

        if (auditLog.getHasDoc()) {
            FileDocument fileDocument = fileRepository.findById(auditLog.getFileId())
                    .orElseThrow(() -> new NotFoundException("File not found"));
            return googleCloudService.generateSignedUrl(fileDocument.getUrl(), 15);
        } else {
            return "Document doesn't has an old file attached to it!";
        }
    }

    @Override
    public void deleteOldReprovedDocuments() {
        List<String> fileIds = new ArrayList<>();
        List<AuditLogDocument> updateAuditLogs = new ArrayList<>();
        List<AuditLogDocument> auditLogs = auditLogDocumentRepository.findAllByDocumentIdIsNotNull();
        for (AuditLogDocument auditLog : auditLogs) {
            fileIds.add(auditLog.getFileId());
            updateAuditLogs.add(auditLog);
        }
        List<FileDocument> fileDocuments = fileRepository.findAllById(fileIds);
        for (FileDocument fileDocument : fileDocuments) {
            if (ChronoUnit.MONTHS.between(fileDocument.getCreationDate(), LocalDate.now()) >= 1
                && fileDocument.getStatus().equals(DocumentStatusEnum.REPROVADO)) {
                if (fileDocument.getUrl() != null) try {
                    googleCloudService.deleteFile(fileDocument.getUrl());
                    List<AuditLogDocument> logs = updateAuditLogs.stream()
                            .filter(auditLogDocument ->
                                    auditLogDocument.getFileId().equals(fileDocument.getId()))
                            .collect(Collectors.toList());
                    logs.forEach(auditLogDocument -> auditLogDocument.setFileId(null));
                    auditLogDocumentRepository.saveAll(logs);
                    fileRepository.delete(fileDocument);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    @Transactional
    public void documentValidityCheck(DocumentValidityEnum documentValidityEnum) {
        Pageable pageable = PageRequest.of(0,
                50,
                Sort.by(Sort.Order.asc("creationDate"),
                        Sort.Order.asc("idDocumentation")));
        Page<Document> documents = documentRepository.findAllByValidityAndContractStatus(documentValidityEnum, ContractStatusEnum.ACTIVE, pageable);
        while (documents.hasContent()) {
            List<Document> filteredDocuments = documents.stream()
                    .filter(document -> !(document instanceof DocumentEmployee documentEmployee
                            && documentEmployee.getEmployee().getContractEmployees().isEmpty()))
                    .toList();
            List<Document> documentBatch = new ArrayList<>(50);
            List<Document> updateDocumentValidityDoneBatch = new ArrayList<>(50);
            List<ContractDocument> contractDocumentBatch = new ArrayList<>(50);
            for (Document document : filteredDocuments) {
                if (!document.getDocumentMatrix().getIsValidityFixed()
                    || (document.getDocumentMatrix().getFixedValidityAt() != null
                        && document.getDocumentMatrix().getFixedValidityAt().getMonth() == LocalDate.now().getMonth()
                        && document.getDocumentMatrix().getFixedValidityAt().getDayOfMonth() == LocalDate.now().getDayOfMonth())) {
                    if (document.getValidity().equals(DocumentValidityEnum.INDEFINITE)) {
                        continue;
                    } else {
                        String newTitle = switch (document.getValidity()) {
                            case WEEKLY  -> document.getWeeklyTitle();
                            case MONTHLY -> document.getMonthlyTitle();
                            case ANNUAL  -> document.getAnnualTitle();
                            default -> throw new IllegalStateException("Unexpected value: " + document.getValidity());
                        };
                        Document newDocument = null;
                        if (document instanceof DocumentEmployee documentEmployee) {
                            newDocument = DocumentEmployee.builder()
                                    .title(newTitle)
                                    .type(document.getType())
                                    .expirationDate(document.getExpirationDate())
                                    .expirationDateUnit(document.getExpirationDateUnit())
                                    .validity(documentValidityEnum)
                                    .doesBlock(document.getDoesBlock())
                                    .required(document.getRequired())
                                    .documentMatrix(document.getDocumentMatrix())
                                    .employee(documentEmployee.getEmployee())
                                    .build();
                        } else if (document instanceof DocumentProviderSupplier documentProviderSupplier) {
                            newDocument = DocumentProviderSupplier.builder()
                                    .title(newTitle)
                                    .type(document.getType())
                                    .expirationDate(document.getExpirationDate())
                                    .expirationDateUnit(document.getExpirationDateUnit())
                                    .validity(documentValidityEnum)
                                    .doesBlock(document.getDoesBlock())
                                    .required(document.getRequired())
                                    .documentMatrix(document.getDocumentMatrix())
                                    .providerSupplier(documentProviderSupplier.getProviderSupplier())
                                    .build();
                        } else if (document instanceof DocumentProviderSubcontractor documentProviderSubcontractor) {
                            newDocument = DocumentProviderSubcontractor.builder()
                                    .title(newTitle)
                                    .type(document.getType())
                                    .expirationDate(document.getExpirationDate())
                                    .expirationDateUnit(document.getExpirationDateUnit())
                                    .validity(documentValidityEnum)
                                    .doesBlock(document.getDoesBlock())
                                    .required(document.getRequired())
                                    .documentMatrix(document.getDocumentMatrix())
                                    .providerSubcontractor(documentProviderSubcontractor.getProviderSubcontractor())
                                    .build();
                        }
                        document.setIsValidityDone(true);
                        updateDocumentValidityDoneBatch.add(document);
                        documentBatch.add(newDocument);
                        List<Contract> contracts = document.getContractDocuments().stream()
                                .map(ContractDocument::getContract)
                                .filter(contract -> contract.getStatus().equals(ContractStatusEnum.ACTIVE))
                                .toList();
                        for (Contract contract : contracts) {
                            contractDocumentBatch.add(ContractDocument.builder()
                                    .document(newDocument)
                                    .contract(contract)
                                    .build());
                        }
                    }
                }
                if (updateDocumentValidityDoneBatch.size() >= 50
                        || documentBatch.size() >= 50
                        || contractDocumentBatch.size() >= 50) {
                    documentRepository.saveAll(updateDocumentValidityDoneBatch);
                    documentRepository.saveAll(documentBatch);
                    contractDocumentRepository.saveAll(contractDocumentBatch);
                    updateDocumentValidityDoneBatch.clear();
                    documentBatch.clear();
                    contractDocumentBatch.clear();
                }
            }
            if (!updateDocumentValidityDoneBatch.isEmpty()) {
                documentRepository.saveAll(updateDocumentValidityDoneBatch);
                updateDocumentValidityDoneBatch.clear();
            }
            if (!documentBatch.isEmpty()) {
                documentRepository.saveAll(documentBatch);
                documentBatch.clear();
            }
            if (!contractDocumentBatch.isEmpty()) {
                contractDocumentRepository.saveAll(contractDocumentBatch);
                contractDocumentBatch.clear();
            }

            if (documents.hasNext()) {
                documents = documentRepository.findAllByValidityAndContractStatus(documentValidityEnum, ContractStatusEnum.ACTIVE, documents.nextPageable());
            } else {
                break;
            }
        }
    }

    @Override
    public void deleteOverwrittenDocuments() {
        Pageable pageable = PageRequest.of(0, 50);
        Page<FileDocument> files = fileRepository.findAllByCanBeOverwritten(true, pageable);
        List<FileDocument> fileBatch = new ArrayList<>(50);
        while (files.hasContent()) {
            for (FileDocument fileDocument : files) {
                if (fileDocument.getCanBeOverwritten()) {
                    try {
                        googleCloudService.deleteFile(fileDocument.getUrl());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    fileDocument.setUrl(null);
                    fileDocument.setDeleted(true);
                    fileBatch.add(fileDocument);

                    if (fileBatch.size() >= 50) {
                        fileRepository.saveAll(fileBatch);
                        fileBatch.clear();
                    }
                }
                if (!fileBatch.isEmpty()) {
                    fileRepository.saveAll(fileBatch);
                    fileBatch.clear();
                }
            }

            if (files.hasNext()) {
                files = fileRepository.findAllByCanBeOverwritten(true, files.nextPageable());
            } else {
                break;
            }
        }
    }

    @Override
    public void deleteEndLifeDocument() {
        Pageable pageable = PageRequest.of(0, 50);
        LocalDateTime endLifeTime = LocalDateTime.now().minusYears(5);
        Date cutOffDate = Date.from(endLifeTime.atZone(ZoneId.systemDefault()).toInstant());
        List<ContractStatusEnum> statuses = new ArrayList<>();
        statuses.add(ContractStatusEnum.FINISHED);
        statuses.add(ContractStatusEnum.SUSPENDED);
        Page<FileDocument> files = fileRepository.findAllUploadedBeforeThanAndNotDeletedAndContractStatuses(
                endLifeTime,
                statuses,
                pageable);
        while (files.hasContent()) {
            List<FileDocument> fileBatch = new ArrayList<>(50);
            for (FileDocument file : files.getContent()) {
                if (file.getDocument().getContractDocuments().stream().noneMatch(contractDocument -> statuses.contains(contractDocument.getContract().getStatus())
                        && contractDocument.getContract().getEndDate() != null
                        && contractDocument.getContract().getEndDate().before(cutOffDate))) {
                    try {
                        googleCloudService.deleteFile(file.getUrl());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    file.setUrl(null);
                    file.setDeleted(true);
                    fileBatch.add(file);
                    if (fileBatch.size() >= 50) {
                        fileRepository.saveAll(fileBatch);
                        fileBatch.clear();
                    }
                }
            }
            if (!fileBatch.isEmpty()) {
                fileRepository.saveAll(fileBatch);
                fileBatch.clear();
            }

            if (files.hasNext()) {
                files = fileRepository.findAllUploadedBeforeThanAndNotDeletedAndContractStatuses(
                        endLifeTime,
                        statuses,
                        files.nextPageable());
            }
        }
    }
}

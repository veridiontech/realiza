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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
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
            if (documentBranches.isEmpty()) {
                throw new NotFoundException("Document branch not found");
            }
            expirationUnit = documentBranches.get(documentBranches.size() - 1).getExpirationDateUnit();
            expirationAmount = documentBranches.get(documentBranches.size() - 1).getExpirationDateAmount();
            if (expirationAmount == null) {
                expirationAmount = document.getDocumentMatrix().getExpirationDateAmount();
            }
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
        if (document.getStatus() == APROVADO) {
            List<AuditLogDocument> auditLogDocuments = auditLogDocumentRepository.findAllByDocumentId(document.getIdDocumentation());
            auditLogDocuments.forEach(auditLogDocument -> auditLogDocument.setHasDoc(false));
            auditLogDocumentRepository.saveAll(auditLogDocuments);
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
                        ChronoUnit.DAYS.between(document.getVersionDate(), LocalDateTime.now()) + " dias entre o upload e a validação",
                        action,
                        userResponsible.getIdUser());
            }
        }

        return "Document status changed to " + documentStatusChangeRequestDto.getStatus().name();
    }

    @Override
    public String documentExemptionRequest(String documentId, String contractId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Document not found"));

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        ContractDocument contractDocument = document.getContractDocuments().stream()
                .filter(cd -> cd.getContract().equals(contract))
                .toList().get(0);

        contractDocument.setStatus(ISENCAO_PENDENTE);
        contractDocumentRepository.save(contractDocument);

        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            crudItemManagementImpl.saveDocumentSolicitation(ItemManagementDocumentRequestDto.builder()
                            .idRequester(userResponsible != null ? userResponsible.getIdUser() : null)
                    .solicitationType(ItemManagement.SolicitationType.EXEMPTION)
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
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    updateAuditLogs.forEach(auditLogDocument -> auditLogDocument.setFileId(null));
                    auditLogDocumentRepository.saveAll(updateAuditLogs);
                    fileRepository.delete(fileDocument);
                }
            }
        }
    }
}

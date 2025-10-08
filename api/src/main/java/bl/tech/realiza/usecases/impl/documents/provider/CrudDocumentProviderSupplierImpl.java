package bl.tech.realiza.usecases.impl.documents.provider;

import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.documents.provider.DocumentProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentMatrixResponseDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.services.GoogleCloudService;
import bl.tech.realiza.services.auth.JwtService;
import bl.tech.realiza.services.documentProcessing.DocumentProcessingService;
import bl.tech.realiza.usecases.interfaces.auditLogs.AuditLogService;
import bl.tech.realiza.usecases.interfaces.documents.provider.CrudDocumentProviderSupplier;
import bl.tech.realiza.usecases.interfaces.users.CrudNotification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static bl.tech.realiza.domains.documents.Document.Status.*;
import static bl.tech.realiza.domains.enums.AuditLogActionsEnum.*;
import static bl.tech.realiza.domains.enums.AuditLogTypeEnum.*;

@Service
@RequiredArgsConstructor
public class CrudDocumentProviderSupplierImpl implements CrudDocumentProviderSupplier {

    private final DocumentProviderSupplierRepository documentSupplierRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final FileRepository fileRepository;
    private final DocumentMatrixRepository documentMatrixRepository;
    private final DocumentProcessingService documentProcessingService;
    private final UserRepository userRepository;
    private final AuditLogService auditLogServiceImpl;
    private final GoogleCloudService googleCloudService;
    private final CrudNotification crudNotification;

    @Override
    public DocumentResponseDto save(DocumentProviderSupplierRequestDto documentProviderSupplierRequestDto) {
        if (documentProviderSupplierRequestDto.getSupplier() == null || documentProviderSupplierRequestDto.getSupplier().isEmpty()) {
            throw new BadRequestException("Invalid supplier");
        }

        ProviderSupplier providerSupplier = providerSupplierRepository.findById(documentProviderSupplierRequestDto.getSupplier())
                .orElseThrow(() -> new EntityNotFoundException("Provider supplier not found"));

        DocumentMatrix matrix = documentMatrixRepository.findById(documentProviderSupplierRequestDto.getDocumentMatrixId())
                .orElseThrow(() -> new NotFoundException("Document Matrix not found"));

        DocumentProviderSupplier savedDocumentSupplier = documentSupplierRepository.save(DocumentProviderSupplier.builder()
                .title(documentProviderSupplierRequestDto.getTitle())
                .status(documentProviderSupplierRequestDto.getStatus())
                .providerSupplier(providerSupplier)
                        .documentMatrix(matrix)
                .build());

        return DocumentResponseDto.builder()
                .idDocument(savedDocumentSupplier.getIdDocumentation())
                .title(savedDocumentSupplier.getTitle())
                .status(savedDocumentSupplier.getStatus())
                .creationDate(savedDocumentSupplier.getCreationDate())
                .supplier(savedDocumentSupplier.getProviderSupplier() != null
                        ? savedDocumentSupplier.getProviderSupplier().getIdProvider()
                        : null)
                .build();
    }

    @Override
    public Optional<DocumentResponseDto> findOne(String id) {
        DocumentProviderSupplier documentSupplier = documentSupplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document supplier not found"));

        String signedUrl = null;
        FileDocument fileDocument = documentSupplier.getDocument().stream()
                .max(Comparator.comparing(FileDocument::getCreationDate))
                .orElse(null);
        if (fileDocument != null) {
            if (fileDocument.getUrl() != null) {
                signedUrl = googleCloudService.generateSignedUrl(fileDocument.getUrl(), 15);
            }
        }

        DocumentResponseDto documentSupplierResponse = DocumentResponseDto.builder()
                .idDocument(documentSupplier.getIdDocumentation())
                .title(documentSupplier.getTitle())
                .status(documentSupplier.getStatus())
                .signedUrl(signedUrl)
                .creationDate(documentSupplier.getCreationDate())
                .supplier(documentSupplier.getProviderSupplier() != null
                        ? documentSupplier.getProviderSupplier().getIdProvider()
                        : null)
                .build();

        return Optional.of(documentSupplierResponse);
    }

    @Override
    public Page<DocumentResponseDto> findAll(Pageable pageable) {
        Page<DocumentProviderSupplier> documentSupplierPage = documentSupplierRepository.findAll(pageable);

        return documentSupplierPage.map(
                documentSupplier -> {
                    String signedUrl = null;
                    FileDocument fileDocument = documentSupplier.getDocument().stream()
                            .max(Comparator.comparing(FileDocument::getCreationDate))
                            .orElse(null);
                    if (fileDocument != null) {
                        if (fileDocument.getUrl() != null) {
                            signedUrl = googleCloudService.generateSignedUrl(fileDocument.getUrl(), 15);
                        }
                    }

                    return DocumentResponseDto.builder()
                            .idDocument(documentSupplier.getIdDocumentation())
                            .title(documentSupplier.getTitle())
                            .status(documentSupplier.getStatus())
                            .signedUrl(signedUrl)
                            .creationDate(documentSupplier.getCreationDate())
                            .supplier(documentSupplier.getProviderSupplier() != null
                                    ? documentSupplier.getProviderSupplier().getIdProvider()
                                    : null)
                            .build();
                }
        );
    }

    @Override
    public Optional<DocumentResponseDto> update(String id, DocumentProviderSupplierRequestDto documentProviderSupplierRequestDto) {
        DocumentProviderSupplier documentSupplier = documentSupplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document supplier not found"));

        documentSupplier.setStatus(documentProviderSupplierRequestDto.getStatus() != null
                ? documentProviderSupplierRequestDto.getStatus()
                : documentSupplier.getStatus());

        DocumentProviderSupplier savedDocumentSupplier = documentSupplierRepository.save(documentSupplier);

        DocumentResponseDto documentSupplierResponse = DocumentResponseDto.builder()
                .idDocument(savedDocumentSupplier.getIdDocumentation())
                .title(savedDocumentSupplier.getTitle())
                .status(savedDocumentSupplier.getStatus())
                .creationDate(savedDocumentSupplier.getCreationDate())
                .supplier(savedDocumentSupplier.getProviderSupplier() != null
                        ? savedDocumentSupplier.getProviderSupplier().getIdProvider()
                        : null)
                .build();

        return Optional.of(documentSupplierResponse);
    }

    @Override
    public void delete(String id) {
        documentSupplierRepository.deleteById(id);
    }

    @Override
    public Optional<DocumentResponseDto> upload(String id, MultipartFile file) throws IOException {
        if (file != null) {
            if (file.getSize() > 5 * 1024 * 1024) { // 5 MB
                throw new BadRequestException("Arquivo muito grande.");
            }
        }
        FileDocument savedFileDocument = null;
        String signedUrl = null;

        DocumentProviderSupplier documentSupplier = documentSupplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document supplier not found"));

        if (file != null && !file.isEmpty()) {
            try {
                String gcsUrl = googleCloudService.uploadFile(file, "documents/supplier");

                savedFileDocument = fileRepository.save(FileDocument.builder()
                        .name(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .url(gcsUrl)
                        .document(documentSupplier)
                        .canBeOverwritten(documentSupplier.getDocumentMatrix().getIsDocumentUnique())
                        .build());
                signedUrl = googleCloudService.generateSignedUrl(savedFileDocument.getUrl(), 15);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new EntityNotFoundException(e);
            }
            documentSupplier.setStatus(EM_ANALISE);
            documentSupplier.setAdherent(true);
            documentSupplier.setConforming(false);
        }

        DocumentProviderSupplier savedDocumentSupplier = documentSupplierRepository.save(documentSupplier);

        documentProcessingService.processDocumentAsync(file,
                (DocumentProviderSupplier) Hibernate.unproxy(documentSupplier));

        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            if (userResponsible != null) {
                auditLogServiceImpl.createAuditLog(
                        savedDocumentSupplier.getIdDocumentation(),
                        DOCUMENT,
                        userResponsible.getFullName() + " fez upload do documento "
                                + savedDocumentSupplier.getTitle() + " para a empresa "
                                + (savedDocumentSupplier.getProviderSupplier() != null
                                ? savedDocumentSupplier.getProviderSupplier().getCorporateName()
                                : "Not identified"),
                        null,
                        null,
                        UPLOAD,
                        userResponsible.getIdUser());
            }
        }

        crudNotification.saveDocumentNotificationForRealizaUsers(savedDocumentSupplier.getIdDocumentation());

        DocumentResponseDto documentSupplierResponse = DocumentResponseDto.builder()
                .idDocument(savedDocumentSupplier.getIdDocumentation())
                .title(savedDocumentSupplier.getTitle())
                .status(savedDocumentSupplier.getStatus())
                .signedUrl(signedUrl)
                .creationDate(savedDocumentSupplier.getCreationDate())
                .supplier(savedDocumentSupplier.getProviderSupplier() != null
                        ? savedDocumentSupplier.getProviderSupplier().getIdProvider()
                        : null)
                .build();

        return Optional.of(documentSupplierResponse);
    }

    @Override
    public Page<DocumentResponseDto> findAllBySupplier(String idSearch, Pageable pageable) {
        Page<DocumentProviderSupplier> documentSupplierPage = documentSupplierRepository.findAllByProviderSupplier_IdProviderAndIsActive(idSearch, pageable, true);

        return documentSupplierPage.map(
                documentSupplier -> {
                    String signedUrl = null;
                    FileDocument fileDocument = documentSupplier.getDocument().stream()
                            .max(Comparator.comparing(FileDocument::getCreationDate))
                            .orElse(null);
                    if (fileDocument != null) {
                        if (fileDocument.getUrl() != null) {
                            signedUrl = googleCloudService.generateSignedUrl(fileDocument.getUrl(), 15);
                        }
                    }

                    return DocumentResponseDto.builder()
                            .idDocument(documentSupplier.getIdDocumentation())
                            .title(documentSupplier.getTitle())
                            .status(documentSupplier.getStatus())
                            .signedUrl(signedUrl)
                            .creationDate(documentSupplier.getCreationDate())
                            .supplier(documentSupplier.getProviderSupplier() != null
                                    ? documentSupplier.getProviderSupplier().getIdProvider()
                                    : null)
                            .build();
                }
        );
    }

    @Override
    public DocumentResponseDto findAllSelectedDocuments(String id) {
        providerSupplierRepository.findById(id).orElseThrow(() -> new NotFoundException("Supplier not found"));
        List<DocumentProviderSupplier> documentSupplier = documentSupplierRepository.findAllByProviderSupplier_IdProviderAndIsActive(id, true);
        List<DocumentMatrixResponseDto> selectedDocuments = documentSupplier.stream()
                .sorted(Comparator.comparing(db -> db.getDocumentMatrix().getName()))
                .map(doc -> DocumentMatrixResponseDto.builder()
                        .documentId(doc.getIdDocumentation()) // ID do DocumentBranch
                        .idDocumentMatrix(doc.getDocumentMatrix().getIdDocument())
                        .name(doc.getTitle())
//                        .idDocumentSubgroup(doc.getDocumentMatrix() != null
//                                ? (doc.getDocumentMatrix().getSubGroup() != null
//                                    ? doc.getDocumentMatrix().getSubGroup().getIdDocumentSubgroup()
//                                    : null)
//                                : null)
//                        .subgroupName(doc.getDocumentMatrix() != null
//                                ? (doc.getDocumentMatrix().getSubGroup() != null
//                                    ? doc.getDocumentMatrix().getSubGroup().getSubgroupName()
//                                    : null)
//                                : null)
                        .idDocumentGroup(doc.getDocumentMatrix().getGroup() != null
                                ? doc.getDocumentMatrix().getGroup().getIdDocumentGroup()
                                : null)
                        .groupName(doc.getDocumentMatrix().getGroup() != null
                                ? doc.getDocumentMatrix().getGroup().getGroupName()
                                : null)
                        .build())
                .collect(Collectors.toList());
        List<DocumentMatrixResponseDto> allDocuments = documentMatrixRepository.findAllByGroup_GroupName("Documentos empresa-serviço")
                .stream()
                .sorted(Comparator.comparing(DocumentMatrix::getName))
                .map(doc -> DocumentMatrixResponseDto.builder()
                        .idDocumentMatrix(doc.getIdDocument())
                        .name(doc.getName())
//                        .idDocumentSubgroup(doc.getSubGroup() != null
//                                ? doc.getSubGroup().getIdDocumentSubgroup()
//                                : null)
//                        .subgroupName(doc.getSubGroup() != null
//                                ? doc.getSubGroup().getSubgroupName()
//                                : null)
                        .idDocumentGroup(doc.getGroup() != null
                                ? doc.getGroup().getIdDocumentGroup()
                                : null)
                        .groupName(doc.getGroup() != null
                                ? doc.getGroup().getGroupName()
                                : null)
                        .build())
                .toList();
        List<DocumentMatrixResponseDto> nonSelectedDocuments = new ArrayList<>(allDocuments);
        nonSelectedDocuments.removeAll(selectedDocuments);
        DocumentResponseDto supplierResponse = DocumentResponseDto.builder()
                .selectedDocumentsEnterprise(selectedDocuments)
                .nonSelectedDocumentsEnterprise(nonSelectedDocuments)
                .build();

        return supplierResponse;
    }

    @Override
    public String updateRequiredDocuments(String id, List<String> documentCollection) {
        if (documentCollection == null || documentCollection.isEmpty()) {
            throw new NotFoundException("Invalid documents");
        }

        ProviderSupplier providerSupplier = providerSupplierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Supplier not found"));

        List<DocumentMatrix> documentMatrixList = documentMatrixRepository.findAllById(documentCollection);
        if (documentMatrixList.isEmpty()) {
            throw new NotFoundException("Documents not found");
        }

        List<DocumentProviderSupplier> existingDocumentSuppliers = documentSupplierRepository.findAllByProviderSupplier_IdProviderAndIsActive(id, true);

        Set<DocumentMatrix> existingDocuments = existingDocumentSuppliers.stream()
                .map(DocumentProviderSupplier::getDocumentMatrix)
                .collect(Collectors.toSet());

        List<DocumentProviderSupplier> newDocumentSuppliers = documentMatrixList.stream()
                .filter(doc -> !existingDocuments.contains(doc))
                .map(doc -> DocumentProviderSupplier.builder()
                        .title(doc.getName())
                        .status(PENDENTE)
                        .providerSupplier(providerSupplier)
                        .documentMatrix(doc)
                        .build())
                .collect(Collectors.toList());

        List<DocumentProviderSupplier> documentsToRemove = existingDocumentSuppliers.stream()
                .filter(db -> !documentMatrixList.contains(db.getDocumentMatrix()))
                .collect(Collectors.toList());

        if (!documentsToRemove.isEmpty()) {
            documentSupplierRepository.deleteAll(documentsToRemove);
        }

        if (!newDocumentSuppliers.isEmpty()) {
            documentSupplierRepository.saveAll(newDocumentSuppliers);
        }

        return "Documents updated successfully";
    }

    @Override
    public String addRequiredDocument(String idEnterprise, String documentMatrixId) {
        if (documentMatrixId == null || documentMatrixId.isEmpty()) {
            throw new BadRequestException("Invalid documents");
        }

        ProviderSupplier providerSupplier = providerSupplierRepository.findById(idEnterprise)
                .orElseThrow(() -> new NotFoundException("Supplier not found"));

        DocumentMatrix documentMatrix = documentMatrixRepository.findById(documentMatrixId)
                .orElseThrow(() -> new NotFoundException("Document not found in matrix"));

        List<DocumentProviderSupplier> existingDocumentBranches = documentSupplierRepository.findAllByProviderSupplier_IdProviderAndIsActive(idEnterprise, true);

        Set<DocumentMatrix> existingDocuments = existingDocumentBranches.stream()
                .map(DocumentProviderSupplier::getDocumentMatrix)
                .collect(Collectors.toSet());

        DocumentProviderSupplier newDocumentBranch = DocumentProviderSupplier.builder()
                .title(documentMatrix.getName())
                .status(PENDENTE)
                .providerSupplier(providerSupplier)
                .documentMatrix(documentMatrix)
                .build();

        documentSupplierRepository.save(newDocumentBranch);

        return "Document updated successfully";
    }

    @Override
    public void removeRequiredDocument(String documentId) {
        if (documentId == null || documentId.isEmpty()) {
            throw new NotFoundException("Invalid documents");
        }
        documentSupplierRepository.deleteById(documentId);
    }
}

package bl.tech.realiza.usecases.impl.documents.provider;

import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.documents.provider.DocumentProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentMatrixResponseDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.services.GoogleCloudService;
import bl.tech.realiza.services.auth.JwtService;
import bl.tech.realiza.services.documentProcessing.DocumentProcessingService;
import bl.tech.realiza.usecases.interfaces.auditLogs.AuditLogService;
import bl.tech.realiza.usecases.interfaces.documents.provider.CrudDocumentProviderSubcontractor;
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
public class CrudDocumentProviderSubcontractorImpl implements CrudDocumentProviderSubcontractor {

    private final DocumentProviderSubcontractorRepository documentSubcontractorRepository;
    private final ProviderSubcontractorRepository providerSubcontractorRepository;
    private final FileRepository fileRepository;
    private final DocumentMatrixRepository documentMatrixRepository;
    private final DocumentProcessingService documentProcessingService;
    private final UserRepository userRepository;
    private final AuditLogService auditLogServiceImpl;
    private final GoogleCloudService googleCloudService;

    @Override
    public DocumentResponseDto save(DocumentProviderSubcontractorRequestDto documentProviderSubcontractorRequestDto) {
        if (documentProviderSubcontractorRequestDto.getSubcontractor() == null || documentProviderSubcontractorRequestDto.getSubcontractor().isEmpty()) {
            throw new BadRequestException("Invalid subcontractor");
        }

        ProviderSubcontractor providerSubcontractor = providerSubcontractorRepository.findById(documentProviderSubcontractorRequestDto.getSubcontractor())
                .orElseThrow(() -> new EntityNotFoundException("Subcontractor not found"));

        DocumentProviderSubcontractor savedDocumentSubcontractor = documentSubcontractorRepository.save(DocumentProviderSubcontractor.builder()
                .title(documentProviderSubcontractorRequestDto.getTitle())
                .status(documentProviderSubcontractorRequestDto.getStatus())
                .providerSubcontractor(providerSubcontractor)
                .build());

        return DocumentResponseDto.builder()
                .idDocument(savedDocumentSubcontractor.getIdDocumentation())
                .title(savedDocumentSubcontractor.getTitle())
                .status(savedDocumentSubcontractor.getStatus())
                .creationDate(savedDocumentSubcontractor.getCreationDate())
                .subcontractor(savedDocumentSubcontractor.getProviderSubcontractor() != null
                        ? savedDocumentSubcontractor.getProviderSubcontractor().getIdProvider()
                        : null)
                .build();
    }

    @Override
    public Optional<DocumentResponseDto> findOne(String id) {
        DocumentProviderSubcontractor documentSubcontractor = documentSubcontractorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subcontractor not found"));

        String signedUrl = null;
        FileDocument fileDocument = documentSubcontractor.getDocument().stream()
                .max(Comparator.comparing(FileDocument::getCreationDate))
                .orElse(null);
        if (fileDocument != null) {
            if (fileDocument.getUrl() != null) {
                signedUrl = googleCloudService.generateSignedUrl(fileDocument.getUrl(), 15);
            }
        }

        DocumentResponseDto documentSubcontractorResponse = DocumentResponseDto.builder()
                .idDocument(documentSubcontractor.getIdDocumentation())
                .title(documentSubcontractor.getTitle())
                .status(documentSubcontractor.getStatus())
                .signedUrl(signedUrl)
                .creationDate(documentSubcontractor.getCreationDate())
                .subcontractor(documentSubcontractor.getProviderSubcontractor() != null
                        ? documentSubcontractor.getProviderSubcontractor().getIdProvider()
                        : null)
                .build();

        return Optional.of(documentSubcontractorResponse);
    }

    @Override
    public Page<DocumentResponseDto> findAll(Pageable pageable) {
        Page<DocumentProviderSubcontractor> documentSubcontractorPage = documentSubcontractorRepository.findAll(pageable);

        return documentSubcontractorPage.map(
                documentSubcontractor -> {
                    String signedUrl = null;
                    FileDocument fileDocument = documentSubcontractor.getDocument().stream()
                            .max(Comparator.comparing(FileDocument::getCreationDate))
                            .orElse(null);
                    if (fileDocument != null) {
                        if (fileDocument.getUrl() != null) {
                            signedUrl = googleCloudService.generateSignedUrl(fileDocument.getUrl(), 15);
                        }
                    }

                    return DocumentResponseDto.builder()
                            .idDocument(documentSubcontractor.getIdDocumentation())
                            .title(documentSubcontractor.getTitle())
                            .status(documentSubcontractor.getStatus())
                            .signedUrl(signedUrl)
                            .creationDate(documentSubcontractor.getCreationDate())
                            .subcontractor(documentSubcontractor.getProviderSubcontractor() != null
                                    ? documentSubcontractor.getProviderSubcontractor().getIdProvider()
                                    : null)
                            .build();
                }
        );
    }

    @Override
    public Optional<DocumentResponseDto> update(String id, DocumentProviderSubcontractorRequestDto documentProviderSubcontractorRequestDto) {
        DocumentProviderSubcontractor documentSubcontractor = documentSubcontractorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subcontractor not found"));

        documentSubcontractor.setStatus(documentProviderSubcontractorRequestDto.getStatus() != null
                ? documentProviderSubcontractorRequestDto.getStatus()
                : documentSubcontractor.getStatus());

        DocumentProviderSubcontractor savedDocumentSubcontractor = documentSubcontractorRepository.save(documentSubcontractor);

        DocumentResponseDto documentSubcontractorResponse = DocumentResponseDto.builder()
                .idDocument(savedDocumentSubcontractor.getIdDocumentation())
                .title(savedDocumentSubcontractor.getTitle())
                .status(savedDocumentSubcontractor.getStatus())
                .creationDate(savedDocumentSubcontractor.getCreationDate())
                .subcontractor(savedDocumentSubcontractor.getProviderSubcontractor() != null
                        ? savedDocumentSubcontractor.getProviderSubcontractor().getIdProvider()
                        : null)
                .build();

        return Optional.of(documentSubcontractorResponse);
    }

    @Override
    public void delete(String id) {
        documentSubcontractorRepository.deleteById(id);
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

        DocumentProviderSubcontractor documentProviderSubcontractor = documentSubcontractorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DocumentBranch not found"));

        if (file != null && !file.isEmpty()) {
            try {
                String gcsUrl = googleCloudService.uploadFile(file, "branch-documents");

                savedFileDocument = fileRepository.save(FileDocument.builder()
                        .name(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .url(gcsUrl)
                        .document(documentProviderSubcontractor)
                        .canBeOverwritten(documentProviderSubcontractor.getDocumentMatrix().getIsDocumentUnique())
                        .build());
                signedUrl = googleCloudService.generateSignedUrl(savedFileDocument.getUrl(), 15);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new EntityNotFoundException(e);
            }
            documentProviderSubcontractor.setStatus(EM_ANALISE);
            documentProviderSubcontractor.setAdherent(true);
            documentProviderSubcontractor.setConforming(false);
        }

        DocumentProviderSubcontractor savedDocumentSubcontractor = documentSubcontractorRepository.save(documentProviderSubcontractor);

        documentProcessingService.processDocumentAsync(file,
                (DocumentProviderSubcontractor) Hibernate.unproxy(documentProviderSubcontractor));

        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            if (userResponsible != null) {
                auditLogServiceImpl.createAuditLog(
                        savedDocumentSubcontractor.getIdDocumentation(),
                        DOCUMENT,
                        userResponsible.getFullName() + " fez upload do documento "
                                + savedDocumentSubcontractor.getTitle() + " para a empresa "
                                + (savedDocumentSubcontractor.getProviderSubcontractor() != null
                                ? savedDocumentSubcontractor.getProviderSubcontractor().getCorporateName()
                                : "Not identified"),
                        null,
                        null,
                        UPLOAD,
                        userResponsible.getIdUser());
            }
        }

        DocumentResponseDto documentSubcontractorResponse = DocumentResponseDto.builder()
                .idDocument(savedDocumentSubcontractor.getIdDocumentation())
                .title(savedDocumentSubcontractor.getTitle())
                .status(savedDocumentSubcontractor.getStatus())
                .signedUrl(signedUrl)
                .creationDate(savedDocumentSubcontractor.getCreationDate())
                .subcontractor(savedDocumentSubcontractor.getProviderSubcontractor() != null
                        ? savedDocumentSubcontractor.getProviderSubcontractor().getIdProvider()
                        : null)
                .build();

        return Optional.of(documentSubcontractorResponse);
    }

    @Override
    public Page<DocumentResponseDto> findAllBySubcontractor(String idSearch, Pageable pageable) {
        Page<DocumentProviderSubcontractor> documentSubcontractorPage = documentSubcontractorRepository.findAllByProviderSubcontractor_IdProviderAndIsActiveIsTrue(idSearch, pageable);

        return documentSubcontractorPage.map(
                documentSubcontractor -> {
                    String signedUrl = null;
                    FileDocument fileDocument = documentSubcontractor.getDocument().stream()
                            .max(Comparator.comparing(FileDocument::getCreationDate))
                            .orElse(null);
                    if (fileDocument != null) {
                        if (fileDocument.getUrl() != null) {
                            signedUrl = googleCloudService.generateSignedUrl(fileDocument.getUrl(), 15);
                        }
                    }

                    return DocumentResponseDto.builder()
                            .idDocument(documentSubcontractor.getIdDocumentation())
                            .title(documentSubcontractor.getTitle())
                            .status(documentSubcontractor.getStatus())
                            .signedUrl(signedUrl)
                            .creationDate(documentSubcontractor.getCreationDate())
                            .subcontractor(documentSubcontractor.getProviderSubcontractor() != null
                                    ? documentSubcontractor.getProviderSubcontractor().getIdProvider()
                                    : null)
                            .build();
                }
        );
    }

    @Override
    public DocumentResponseDto findAllSelectedDocuments(String id) {
        documentSubcontractorRepository.findById(id).orElseThrow(() -> new NotFoundException("Subcontractor not found"));
        List<DocumentProviderSubcontractor> documentSubcontractor = documentSubcontractorRepository.findAllByProviderSubcontractor_IdProviderAndIsActiveIsTrue(id);
        List<DocumentMatrixResponseDto> selectedDocuments = documentSubcontractor.stream()
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
                        .idDocumentGroup(doc.getDocumentMatrix() != null
                                ? doc.getDocumentMatrix().getGroup() != null
                                    ? doc.getDocumentMatrix().getGroup().getIdDocumentGroup()
                                    : null
                                : null)
                        .groupName(doc.getDocumentMatrix() != null
                                ? doc.getDocumentMatrix().getGroup() != null
                                    ? doc.getDocumentMatrix().getGroup().getGroupName()
                                    : null
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
        DocumentResponseDto subcontractorResponse = DocumentResponseDto.builder()
                .selectedDocumentsEnterprise(selectedDocuments)
                .nonSelectedDocumentsEnterprise(nonSelectedDocuments)
                .build();

        return subcontractorResponse;
    }

    @Override
    public String updateRequiredDocuments(String id, List<String> documentCollection) {
        if (documentCollection == null || documentCollection.isEmpty()) {
            throw new NotFoundException("Invalid documents");
        }

        ProviderSubcontractor providerSubcontractor = providerSubcontractorRepository.findById(id).orElseThrow(() -> new NotFoundException("Subcontractor not found"));

        List<DocumentMatrix> documentMatrixList = documentMatrixRepository.findAllById(documentCollection);
        if (documentMatrixList.isEmpty()) {
            throw new NotFoundException("Documents not found");
        }

        List<DocumentProviderSubcontractor> existingDocumentSubcontractors = documentSubcontractorRepository.findAllByProviderSubcontractor_IdProviderAndIsActiveIsTrue(id);

        Set<DocumentMatrix> existingDocuments = existingDocumentSubcontractors.stream()
                .map(DocumentProviderSubcontractor::getDocumentMatrix)
                .collect(Collectors.toSet());

        List<DocumentProviderSubcontractor> newDocumentSubcontractors = documentMatrixList.stream()
                .filter(doc -> !existingDocuments.contains(doc))
                .map(doc -> DocumentProviderSubcontractor.builder()
                        .title(doc.getName())
                        .status(PENDENTE)
                        .providerSubcontractor(providerSubcontractor)
                        .documentMatrix(doc)
                        .build())
                .collect(Collectors.toList());

        List<DocumentProviderSubcontractor> documentsToRemove = existingDocumentSubcontractors.stream()
                .filter(db -> !documentMatrixList.contains(db.getDocumentMatrix()))
                .collect(Collectors.toList());

        if (!documentsToRemove.isEmpty()) {
            documentSubcontractorRepository.deleteAll(documentsToRemove);
        }

        if (!newDocumentSubcontractors.isEmpty()) {
            documentSubcontractorRepository.saveAll(newDocumentSubcontractors);
        }

        return "Documents updated successfully";
    }

    @Override
    public String addRequiredDocument(String idEnterprise, String documentMatrixId) {
        if (documentMatrixId == null || documentMatrixId.isEmpty()) {
            throw new BadRequestException("Invalid documents");
        }

        ProviderSubcontractor providerSubcontractor = providerSubcontractorRepository.findById(idEnterprise).orElseThrow(() -> new NotFoundException("Subcontractor not found"));

        DocumentMatrix documentMatrix = documentMatrixRepository.findById(documentMatrixId).orElseThrow(() -> new NotFoundException("Document not found in matrix"));

        List<DocumentProviderSubcontractor> existingDocumentBranches = documentSubcontractorRepository.findAllByProviderSubcontractor_IdProviderAndIsActiveIsTrue(idEnterprise);

        Set<DocumentMatrix> existingDocuments = existingDocumentBranches.stream()
                .map(DocumentProviderSubcontractor::getDocumentMatrix)
                .collect(Collectors.toSet());

        DocumentProviderSubcontractor newDocumentBranch = DocumentProviderSubcontractor.builder()
                .title(documentMatrix.getName())
                .status(PENDENTE)
                .providerSubcontractor(providerSubcontractor)
                .documentMatrix(documentMatrix)
                .build();

        documentSubcontractorRepository.save(newDocumentBranch);

        return "Document updated successfully";
    }

    @Override
    public void removeRequiredDocument(String documentId) {
        if (documentId == null || documentId.isEmpty()) {
            throw new NotFoundException("Invalid documents");
        }
        documentSubcontractorRepository.deleteById(documentId);
    }
}

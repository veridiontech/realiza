package bl.tech.realiza.usecases.impl.documents.client;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.serviceType.ServiceType;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.enums.AuditLogActionsEnum;
import bl.tech.realiza.domains.enums.AuditLogTypeEnum;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentBranchRepository;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.documents.client.DocumentBranchRequestDto;
import bl.tech.realiza.gateways.requests.documents.client.DocumentExpirationUpdateRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentExpirationResponseDto;
import bl.tech.realiza.gateways.responses.documents.DocumentMatrixResponseDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.gateways.responses.documents.DocumentSummarizedResponseDto;
import bl.tech.realiza.gateways.responses.queue.SetupMessage;
import bl.tech.realiza.services.auth.JwtService;
import bl.tech.realiza.services.documentProcessing.DocumentProcessingService;
import bl.tech.realiza.services.queue.SetupAsyncQueueProducer;
import bl.tech.realiza.usecases.interfaces.auditLogs.AuditLogService;
import bl.tech.realiza.usecases.interfaces.documents.client.CrudDocumentBranch;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
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
import static bl.tech.realiza.domains.services.FileDocument.Owner.*;

@Service
@RequiredArgsConstructor
public class CrudDocumentBranchImpl implements CrudDocumentBranch {

    private final DocumentBranchRepository documentBranchRepository;
    private final DocumentMatrixRepository documentMatrixRepository;
    private final BranchRepository branchRepository;
    private final FileRepository fileRepository;
    private final DocumentProcessingService documentProcessingService;
    private final UserRepository userRepository;
    private final AuditLogService auditLogServiceImpl;
    private final SetupAsyncQueueProducer setupAsyncQueueProducer;

    @Override
    public Optional<DocumentResponseDto> findOne(String id) {
        DocumentBranch documentBranch = documentBranchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DocumentBranch not found"));

        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentBranch.getDocumentation()));

        FileDocument fileDocument = fileDocumentOptional.orElseThrow(() -> new EntityNotFoundException("FileDocument not found"));

        DocumentResponseDto documentBranchResponse = DocumentResponseDto.builder()
                .idDocument(documentBranch.getIdDocumentation())
                .title(documentBranch.getTitle())
                .status(documentBranch.getStatus())
                .documentation(documentBranch.getDocumentation())
                .fileName(fileDocument.getName())
                .fileContentType(fileDocument.getContentType())
                .fileData(fileDocument.getData())
                .creationDate(documentBranch.getCreationDate())
                .branch(documentBranch.getBranch() != null
                        ? documentBranch.getBranch().getIdBranch()
                        : null)
                .build();

        return Optional.of(documentBranchResponse);
    }

    @Override
    public Page<DocumentResponseDto> findAll(Pageable pageable) {
        Page<DocumentBranch> documentBranchPage = documentBranchRepository.findAll(pageable);

        Page<DocumentResponseDto> documentBranchResponseDtoPage = documentBranchPage.map(
                documentBranch -> {
                    FileDocument fileDocument = null;
                    if (documentBranch.getDocumentation() != null && ObjectId.isValid(documentBranch.getDocumentation())) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentBranch.getDocumentation()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }

                    return DocumentResponseDto.builder()
                            .idDocument(documentBranch.getIdDocumentation())
                            .title(documentBranch.getTitle())
                            .status(documentBranch.getStatus())
                            .documentation(documentBranch.getDocumentation())
                            .fileName(fileDocument != null ? fileDocument.getName() : null)
                            .fileContentType(fileDocument != null ? fileDocument.getContentType() : null)
                            .fileData(fileDocument != null ? fileDocument.getData() : null)
                            .creationDate(documentBranch.getCreationDate())
                            .branch(documentBranch.getBranch() != null
                                    ? documentBranch.getBranch().getIdBranch()
                                    : null)
                            .build();
                }

        );

        return documentBranchResponseDtoPage;
    }

    @Override
    public Optional<DocumentResponseDto> update(String id, DocumentBranchRequestDto documentBranchRequestDto, MultipartFile file) throws IOException {
        FileDocument fileDocument = null;
        String fileDocumentId = null;
        FileDocument savedFileDocument= null;

        DocumentBranch documentBranch = documentBranchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DocumentBranch not found"));

        if (file != null && !file.isEmpty()) {
            try {
                fileDocument = FileDocument.builder()
                        .name(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .data(file.getBytes())
                        .build();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new EntityNotFoundException(e);
            }

            try {
                savedFileDocument = fileRepository.save(fileDocument);
                fileDocumentId = savedFileDocument.getIdDocumentAsString();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw new EntityNotFoundException(e);
            }
            documentBranch.setDocumentation(fileDocumentId);
        }

        documentBranch.setStatus(documentBranchRequestDto.getStatus() != null
                ? documentBranchRequestDto.getStatus()
                : documentBranch.getStatus());

        DocumentBranch savedDocumentBranch = documentBranchRepository.save(documentBranch);

        DocumentResponseDto documentBranchResponse = DocumentResponseDto.builder()
                .idDocument(savedDocumentBranch.getIdDocumentation())
                .title(savedDocumentBranch.getTitle())
                .status(savedDocumentBranch.getStatus())
                .documentation(savedDocumentBranch.getDocumentation())
                .creationDate(savedDocumentBranch.getCreationDate())
                .branch(savedDocumentBranch.getBranch() != null
                        ? savedDocumentBranch.getBranch().getIdBranch()
                        : null)
                .build();

        return Optional.of(documentBranchResponse);
    }

    @Override
    public Optional<DocumentResponseDto> upload(String id, MultipartFile file) throws IOException {
        if (file.getSize() > 5 * 1024 * 1024) { // 5 MB
            throw new BadRequestException("Arquivo muito grande.");
        }
        FileDocument fileDocument = null;
        String fileDocumentId = null;
        FileDocument savedFileDocument= null;


        DocumentBranch documentBranch = documentBranchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DocumentBranch not found"));

        if (file != null && !file.isEmpty()) {
            try {
                fileDocument = FileDocument.builder()
                        .name(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .title(documentBranch.getTitle())
                        .owner(FileDocument.Owner.BRANCH)
                        .ownerId(documentBranch.getBranch() != null
                                ? documentBranch.getBranch().getIdBranch()
                                : null)
                        .data(file.getBytes())
                        .build();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new EntityNotFoundException(e);
            }

            try {
                savedFileDocument = fileRepository.save(fileDocument);
                fileDocumentId = savedFileDocument.getIdDocumentAsString();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw new EntityNotFoundException(e);
            }
            documentBranch.setDocumentation(fileDocumentId);
            documentBranch.setStatus(EM_ANALISE);
            documentBranch.setAdherent(true);
            documentBranch.setConforming(false);
        }

        documentProcessingService.processDocumentAsync(file,
                (DocumentBranch) Hibernate.unproxy(documentBranch));

        DocumentBranch savedDocumentBranch = documentBranchRepository.save(documentBranch);

        DocumentResponseDto documentBranchResponse = DocumentResponseDto.builder()
                .idDocument(savedDocumentBranch.getIdDocumentation())
                .title(savedDocumentBranch.getTitle())
                .status(savedDocumentBranch.getStatus())
                .documentation(savedDocumentBranch.getDocumentation())
                .creationDate(savedDocumentBranch.getCreationDate())
                .branch(savedDocumentBranch.getBranch() != null
                        ? savedDocumentBranch.getBranch().getIdBranch()
                        : null)
                .build();

        return Optional.of(documentBranchResponse);
    }

    @Override
    public Page<DocumentResponseDto> findAllByBranch(String idSearch, Pageable pageable) {
        Page<DocumentBranch> documentBranchPage = documentBranchRepository.findAllByBranch_IdBranchAndIsActiveIsTrue(idSearch, pageable);

        Page<DocumentResponseDto> documentBranchResponseDtoPage = documentBranchPage.map(
                documentBranch -> {
                    FileDocument fileDocument = null;
                    if (documentBranch.getDocumentation() != null && ObjectId.isValid(documentBranch.getDocumentation())) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentBranch.getDocumentation()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }

                    return DocumentResponseDto.builder()
                            .idDocument(documentBranch.getIdDocumentation())
                            .title(documentBranch.getTitle())
                            .status(documentBranch.getStatus())
                            .documentation(documentBranch.getDocumentation())
                            .fileName(fileDocument != null
                                    ? fileDocument.getName()
                                    : null)
                            .fileContentType(fileDocument != null
                                    ? fileDocument.getContentType()
                                    : null)
                            .fileData(fileDocument != null
                                    ? fileDocument.getData()
                                    : null)
                            .creationDate(documentBranch.getCreationDate())
                            .branch(documentBranch.getBranch() != null
                                    ? documentBranch.getBranch().getIdBranch()
                                    : null)
                            .build();
                }

        );

        return documentBranchResponseDtoPage;
    }

    @Override
    public DocumentResponseDto findAllSelectedDocuments(String id) {
        branchRepository.findById(id).orElseThrow(() -> new NotFoundException("Branch not found"));

        Comparator<DocumentMatrix> byName = Comparator.comparing(DocumentMatrix::getName);

        List<DocumentBranch> documentBranch = documentBranchRepository.findAllByBranch_IdBranchAndIsActiveIsTrue(id);

        List<DocumentMatrixResponseDto> selectedDocumentsEnterprise = documentBranch.stream()
                .filter(doc -> "Documento empresa".equals(doc.getDocumentMatrix().getSubGroup().getGroup().getGroupName()))
                .sorted(Comparator.comparing(db -> db.getDocumentMatrix().getName()))
                .map(doc -> DocumentMatrixResponseDto.builder()
                        .documentId(doc.getIdDocumentation()) // ID do DocumentBranch
                        .idDocumentMatrix(doc.getDocumentMatrix() != null
                                ? doc.getDocumentMatrix().getIdDocument()
                                : null)
                        .name(doc.getTitle())
                        .idDocumentSubgroup(doc.getDocumentMatrix() != null
                                ? (doc.getDocumentMatrix().getSubGroup() != null
                                    ? doc.getDocumentMatrix().getSubGroup().getIdDocumentSubgroup()
                                    : null)
                                : null)
                        .subgroupName(doc.getDocumentMatrix() != null
                                ? (doc.getDocumentMatrix().getSubGroup() != null
                                    ? doc.getDocumentMatrix().getSubGroup().getSubgroupName()
                                    : null)
                                : null)
                        .idDocumentGroup(doc.getDocumentMatrix() != null
                                ? (doc.getDocumentMatrix().getSubGroup() != null
                                    ? (doc.getDocumentMatrix().getSubGroup().getGroup() != null
                                        ? doc.getDocumentMatrix().getSubGroup().getGroup().getIdDocumentGroup()
                                        : null)
                                    : null)
                                : null)
                        .groupName(doc.getDocumentMatrix() != null
                                ? (doc.getDocumentMatrix().getSubGroup() != null
                                    ? (doc.getDocumentMatrix().getSubGroup().getGroup() != null
                                        ? doc.getDocumentMatrix().getSubGroup().getGroup().getGroupName()
                                        : null)
                                    : null)
                                : null)
                        .build())
                .collect(Collectors.toList());
        List<DocumentMatrixResponseDto> selectedDocumentsPersonal = documentBranch.stream()
                .filter(doc -> "Documento pessoa".equals(doc.getDocumentMatrix().getSubGroup().getGroup().getGroupName()))
                .sorted(Comparator.comparing(db -> db.getDocumentMatrix().getName()))
                .map(doc -> DocumentMatrixResponseDto.builder()
                        .documentId(doc.getIdDocumentation()) // ID do DocumentBranch
                        .idDocumentMatrix(doc.getDocumentMatrix() != null
                                ? doc.getDocumentMatrix().getIdDocument()
                                : null)
                        .name(doc.getTitle())
                        .idDocumentSubgroup(doc.getDocumentMatrix() != null
                                ? (doc.getDocumentMatrix().getSubGroup() != null
                                    ? doc.getDocumentMatrix().getSubGroup().getIdDocumentSubgroup()
                                    : null)
                                : null)
                        .subgroupName(doc.getDocumentMatrix() != null
                                ? (doc.getDocumentMatrix().getSubGroup() != null
                                    ? doc.getDocumentMatrix().getSubGroup().getSubgroupName()
                                    : null)
                                : null)
                        .idDocumentGroup(doc.getDocumentMatrix() != null
                                ? (doc.getDocumentMatrix().getSubGroup() != null
                                    ? (doc.getDocumentMatrix().getSubGroup().getGroup() != null
                                        ? doc.getDocumentMatrix().getSubGroup().getGroup().getIdDocumentGroup()
                                        : null)
                                    : null)
                                : null)
                        .groupName(doc.getDocumentMatrix() != null
                                ? (doc.getDocumentMatrix().getSubGroup() != null
                                    ? (doc.getDocumentMatrix().getSubGroup().getGroup() != null
                                        ? doc.getDocumentMatrix().getSubGroup().getGroup().getGroupName()
                                        : null)
                                    : null)
                                : null)
                        .build())
                .collect(Collectors.toList());
        List<DocumentMatrixResponseDto> selectedDocumentsService = documentBranch.stream()
                .filter(doc -> "Documentos empresa-serviço".equals(doc.getDocumentMatrix().getSubGroup().getGroup().getGroupName()))
                .sorted(Comparator.comparing(db -> db.getDocumentMatrix().getName()))
                .map(doc -> DocumentMatrixResponseDto.builder()
                        .documentId(doc.getIdDocumentation()) // ID do DocumentBranch
                        .idDocumentMatrix(doc.getDocumentMatrix() != null
                                ? doc.getDocumentMatrix().getIdDocument()
                                : null)
                        .name(doc.getTitle())
                        .idDocumentSubgroup(doc.getDocumentMatrix() != null
                                ? (doc.getDocumentMatrix().getSubGroup() != null
                                    ? doc.getDocumentMatrix().getSubGroup().getIdDocumentSubgroup()
                                    : null)
                                : null)
                        .subgroupName(doc.getDocumentMatrix() != null
                                ? (doc.getDocumentMatrix().getSubGroup() != null
                                    ? doc.getDocumentMatrix().getSubGroup().getSubgroupName()
                                    : null)
                                : null)
                        .idDocumentGroup(doc.getDocumentMatrix() != null
                                ? (doc.getDocumentMatrix().getSubGroup() != null
                                    ? (doc.getDocumentMatrix().getSubGroup().getGroup() != null
                                        ? doc.getDocumentMatrix().getSubGroup().getGroup().getIdDocumentGroup()
                                        : null)
                                    : null)
                                : null)
                        .groupName(doc.getDocumentMatrix() != null
                                ? (doc.getDocumentMatrix().getSubGroup() != null
                                    ? (doc.getDocumentMatrix().getSubGroup().getGroup() != null
                                        ? doc.getDocumentMatrix().getSubGroup().getGroup().getGroupName()
                                        : null)
                                    : null)
                                : null)
                        .build())
                .collect(Collectors.toList());
        List<DocumentMatrixResponseDto> selectedDocumentsTraining = documentBranch.stream()
                .filter(doc -> "Treinamentos e certificações".equals(doc.getDocumentMatrix().getSubGroup().getGroup().getGroupName()))
                .sorted(Comparator.comparing(db -> db.getDocumentMatrix().getName()))
                .map(doc -> DocumentMatrixResponseDto.builder()
                        .documentId(doc.getIdDocumentation()) // ID do DocumentBranch
                        .idDocumentMatrix(doc.getDocumentMatrix() != null
                                ? doc.getDocumentMatrix().getIdDocument()
                                : null)
                        .name(doc.getTitle())
                        .idDocumentSubgroup(doc.getDocumentMatrix() != null
                                ? (doc.getDocumentMatrix().getSubGroup() != null
                                    ? doc.getDocumentMatrix().getSubGroup().getIdDocumentSubgroup()
                                    : null)
                                : null)
                        .subgroupName(doc.getDocumentMatrix() != null
                                ? (doc.getDocumentMatrix().getSubGroup() != null
                                    ? doc.getDocumentMatrix().getSubGroup().getSubgroupName()
                                    : null)
                                : null)
                        .idDocumentGroup(doc.getDocumentMatrix() != null
                                ? (doc.getDocumentMatrix().getSubGroup() != null
                                    ? (doc.getDocumentMatrix().getSubGroup().getGroup() != null
                                        ? doc.getDocumentMatrix().getSubGroup().getGroup().getIdDocumentGroup()
                                        : null)
                                    : null)
                                : null)
                        .groupName(doc.getDocumentMatrix() != null
                                ? (doc.getDocumentMatrix().getSubGroup() != null
                                    ? (doc.getDocumentMatrix().getSubGroup().getGroup() != null
                                        ? doc.getDocumentMatrix().getSubGroup().getGroup().getGroupName()
                                        : null)
                                    : null)
                                : null)
                        .build())
                .collect(Collectors.toList());

        List<DocumentMatrixResponseDto> allDocumentsEnterprise = documentMatrixRepository.findAllBySubGroup_Group_GroupName("Documento empresa")
                .stream()
                .sorted(Comparator.comparing(DocumentMatrix::getName))
                .map(doc -> DocumentMatrixResponseDto.builder()
                        .idDocumentMatrix(doc.getIdDocument())
                        .name(doc.getName())
                        .idDocumentSubgroup(doc.getSubGroup() != null
                                ? doc.getSubGroup().getIdDocumentSubgroup()
                                : null)
                        .subgroupName(doc.getSubGroup() != null
                                ? doc.getSubGroup().getSubgroupName()
                                : null)
                        .idDocumentGroup(doc.getSubGroup() != null
                                ? (doc.getSubGroup().getGroup() != null
                                    ? doc.getSubGroup().getGroup().getIdDocumentGroup()
                                    : null)
                                : null)
                        .groupName(doc.getSubGroup() != null
                                ? (doc.getSubGroup().getGroup() != null
                                    ? doc.getSubGroup().getGroup().getGroupName()
                                    : null)
                                : null)
                        .build())
                .toList();
        List<DocumentMatrixResponseDto> allDocumentsPersonal = documentMatrixRepository.findAllBySubGroup_Group_GroupName("Documento pessoa")
                .stream()
                .sorted(Comparator.comparing(DocumentMatrix::getName))
                .map(doc -> DocumentMatrixResponseDto.builder()
                        .idDocumentMatrix(doc.getIdDocument())
                        .name(doc.getName())
                        .idDocumentSubgroup(doc.getSubGroup() != null
                                ? doc.getSubGroup().getIdDocumentSubgroup()
                                : null)
                        .subgroupName(doc.getSubGroup() != null
                                ? doc.getSubGroup().getSubgroupName()
                                : null)
                        .idDocumentGroup(doc.getSubGroup() != null
                                ? (doc.getSubGroup().getGroup() != null
                                    ? doc.getSubGroup().getGroup().getIdDocumentGroup()
                                    : null)
                                : null)
                        .groupName(doc.getSubGroup() != null
                                ? (doc.getSubGroup().getGroup() != null
                                    ? doc.getSubGroup().getGroup().getGroupName()
                                    : null)
                                : null)
                        .build())
                .toList();
        List<DocumentMatrixResponseDto> allDocumentsService = documentMatrixRepository.findAllBySubGroup_Group_GroupName("Documentos empresa-serviço")
                .stream()
                .sorted(Comparator.comparing(DocumentMatrix::getName))
                .map(doc -> DocumentMatrixResponseDto.builder()
                        .idDocumentMatrix(doc.getIdDocument())
                        .name(doc.getName())
                        .idDocumentSubgroup(doc.getSubGroup() != null
                                ? doc.getSubGroup().getIdDocumentSubgroup()
                                : null)
                        .subgroupName(doc.getSubGroup() != null
                                ? doc.getSubGroup().getSubgroupName()
                                : null)
                        .idDocumentGroup(doc.getSubGroup() != null
                                ? (doc.getSubGroup().getGroup() != null
                                    ? doc.getSubGroup().getGroup().getIdDocumentGroup()
                                    : null)
                                : null)
                        .groupName(doc.getSubGroup() != null
                                ? (doc.getSubGroup().getGroup() != null
                                    ? doc.getSubGroup().getGroup().getGroupName()
                                    : null)
                                : null)
                        .build())
                .toList();
        List<DocumentMatrixResponseDto> allDocumentsTraining = documentMatrixRepository.findAllBySubGroup_Group_GroupName("Treinamentos e certificações")
                .stream()
                .sorted(Comparator.comparing(DocumentMatrix::getName))
                .map(doc -> DocumentMatrixResponseDto.builder()
                        .idDocumentMatrix(doc.getIdDocument())
                        .name(doc.getName())
                        .idDocumentSubgroup(doc.getSubGroup() != null
                                ? doc.getSubGroup().getIdDocumentSubgroup()
                                : null)
                        .subgroupName(doc.getSubGroup() != null
                                ? doc.getSubGroup().getSubgroupName()
                                : null)
                        .idDocumentGroup(doc.getSubGroup() != null
                                ? (doc.getSubGroup().getGroup() != null
                                    ? doc.getSubGroup().getGroup().getIdDocumentGroup()
                                    : null)
                                : null)
                        .groupName(doc.getSubGroup() != null
                                ? (doc.getSubGroup().getGroup() != null
                                    ? doc.getSubGroup().getGroup().getGroupName()
                                    : null)
                                : null)
                        .build())
                .toList();

        Set<String> selectedDocumentEnterpriseIds = selectedDocumentsEnterprise.stream()
                .map(DocumentMatrixResponseDto::getIdDocumentMatrix)
                .collect(Collectors.toSet());

        Set<String> selectedDocumentPersonalIds = selectedDocumentsPersonal.stream()
                .map(DocumentMatrixResponseDto::getIdDocumentMatrix)
                .collect(Collectors.toSet());

        Set<String> selectedDocumentServiceIds = selectedDocumentsService.stream()
                .map(DocumentMatrixResponseDto::getIdDocumentMatrix)
                .collect(Collectors.toSet());

        Set<String> selectedDocumentTrainingIds = selectedDocumentsTraining.stream()
                .map(DocumentMatrixResponseDto::getIdDocumentMatrix)
                .collect(Collectors.toSet());

        List<DocumentMatrixResponseDto> nonSelectedDocumentsEnterprise = allDocumentsEnterprise.stream()
                .filter(doc -> !selectedDocumentEnterpriseIds.contains(doc.getIdDocumentMatrix()))
                .collect(Collectors.toList());

        List<DocumentMatrixResponseDto> nonSelectedDocumentsPersonal = allDocumentsPersonal.stream()
                .filter(doc -> !selectedDocumentPersonalIds.contains(doc.getIdDocumentMatrix()))
                .collect(Collectors.toList());

        List<DocumentMatrixResponseDto> nonSelectedDocumentsService = allDocumentsService.stream()
                .filter(doc -> !selectedDocumentServiceIds.contains(doc.getIdDocumentMatrix()))
                .collect(Collectors.toList());

        List<DocumentMatrixResponseDto> nonSelectedDocumentsTraining = allDocumentsTraining.stream()
                .filter(doc -> !selectedDocumentTrainingIds.contains(doc.getIdDocumentMatrix()))
                .collect(Collectors.toList());

        return DocumentResponseDto.builder()
                .selectedDocumentsEnterprise(selectedDocumentsEnterprise)
                .nonSelectedDocumentsEnterprise(nonSelectedDocumentsEnterprise)
                .selectedDocumentsPersonal(selectedDocumentsPersonal)
                .nonSelectedDocumentsPersonal(nonSelectedDocumentsPersonal)
                .selectedDocumentsService(selectedDocumentsService)
                .nonSelectedDocumentsService(nonSelectedDocumentsService)
                .selectedDocumentsTraining(selectedDocumentsTraining)
                .nonSelectedDocumentsTraining(nonSelectedDocumentsTraining)
                .build();
    }

    @Override
    public List<DocumentSummarizedResponseDto> findAllFilteredDocuments(String id, String documentTypeName, Boolean isSelected) {
        branchRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Branch not found"));

        return documentBranchRepository.findFilteredDocumentsSimplified(id, documentTypeName, isSelected);
    }


    @Override
    public List<DocumentExpirationResponseDto> findAllFilteredDocumentsExpiration(String idBranch, String documentTypeName, Boolean isSelected) {
        branchRepository.findById(idBranch)
                .orElseThrow(() -> new NotFoundException("Branch not found"));

        List<DocumentBranch> documentBranch = documentBranchRepository
                .findAllByBranch_IdBranchAndDocumentMatrix_TypeAndIsActive(idBranch, documentTypeName.toLowerCase(), isSelected);

        return documentBranch.stream()
                .sorted(Comparator.comparing(Document::getTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(document -> DocumentExpirationResponseDto.builder()
                        .idDocument(document.getIdDocumentation())
                        .title(document.getTitle())
                        .expirationDateAmount(document.getExpirationDateAmount())
                        .expirationDateUnit(document.getExpirationDateUnit())
                        .build()).toList();
    }

    @Override
    public String updateSelectedDocuments(Boolean isSelected, List<String> documentCollection, Boolean replicate, List<String> branchIds) {
        if (documentCollection == null || documentCollection.isEmpty()) {
            throw new BadRequestException("Invalid documents");
        }

        if (replicate == null) {
            replicate = false;
        }

        List<DocumentBranch> documentList = documentBranchRepository.findAllById(documentCollection);

        if (documentList.isEmpty()) {
            throw new NotFoundException("Documents not found");
        }

        documentList.forEach(documentBranch -> documentBranch.setIsActive(isSelected));

        documentBranchRepository.saveAll(documentList);
        String action = "";
        if (isSelected) {
            action = "selecionou";
        } else {
            action = "desselecionou";
        }
        String branchId = documentList.get(0).getBranch().getIdBranch();
        for (Document document : documentList) {
            if (JwtService.getAuthenticatedUserId() != null) {
                User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                        .orElse(null);
                if (userResponsible != null) {
                    auditLogServiceImpl.createAuditLog(
                            branchId,
                            AuditLogTypeEnum.BRANCH,
                            userResponsible.getFullName() + " " + action + " documento "
                            + document.getTitle(),
                            null,
                            isSelected ? ALLOCATE : DEALLOCATE,
                            userResponsible.getIdUser());
                }
            }

            if (replicate) {
                if (isSelected) {
                    setupAsyncQueueProducer.sendSetup(new SetupMessage("ALLOCATE_DOCUMENT_FROM_BRANCH",
                            null,
                            null,
                            branchIds,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            document.getIdDocumentation(),
                            document.getTitle(),
                            Activity.Risk.LOW,
                            ServiceType.Risk.LOW));
                } else {
                    setupAsyncQueueProducer.sendSetup(new SetupMessage("DEALLOCATE_DOCUMENT_FROM_BRANCH",
                            null,
                            null,
                            branchIds,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            document.getIdDocumentation(),
                            document.getTitle(),
                            Activity.Risk.LOW,
                            ServiceType.Risk.LOW));
                }
            }
        }

        return "Documents updated successfully";
    }

    @Override
    public String addRequiredDocument(String idEnterprise, String documentMatrixId) {
        if (documentMatrixId == null || documentMatrixId.isEmpty()) {
            throw new BadRequestException("Invalid documents");
        }

        Branch branch = branchRepository.findById(idEnterprise)
                .orElseThrow(() -> new NotFoundException("Branch not found"));

        DocumentMatrix documentMatrix = documentMatrixRepository.findById(documentMatrixId)
                .orElseThrow(() -> new NotFoundException("Document not found in matrix"));

        DocumentBranch newDocumentBranch = DocumentBranch.builder()
                        .title(documentMatrix.getName())
                        .status(PENDENTE)
                        .branch(branch)
                        .documentMatrix(documentMatrix)
                        .build();

        documentBranchRepository.save(newDocumentBranch);

        return "Document updated successfully";
    }

    @Override
    public void removeRequiredDocument(String documentId) {
        if (documentId == null || documentId.isEmpty()) {
            throw new NotFoundException("Invalid documents");
        }
        documentBranchRepository.deleteById(documentId);
    }

    @Override
    public DocumentExpirationResponseDto updateSelectedDocumentExpiration(String idDocumentation,
                                                                          DocumentExpirationUpdateRequestDto documentExpirationUpdateRequestDto,
                                                                          Boolean replicate,
                                                                          List<String> branchIds) {
        if (replicate == null) {
            replicate = false;
        }

        DocumentBranch documentBranch = documentBranchRepository.findById(idDocumentation)
                .orElseThrow(() -> new NotFoundException("Document not found"));
        Integer oldAmount = documentBranch.getExpirationDateAmount();
        DocumentMatrix.Unit oldUnit = documentBranch.getExpirationDateUnit();

        documentBranch.setExpirationDateAmount(documentExpirationUpdateRequestDto.getExpirationDateAmount() != null
                ? documentExpirationUpdateRequestDto.getExpirationDateAmount()
                : documentBranch.getExpirationDateAmount());
        documentBranch.setExpirationDateUnit(documentExpirationUpdateRequestDto.getExpirationDateUnit() != null
                ? documentExpirationUpdateRequestDto.getExpirationDateUnit()
                : documentBranch.getExpirationDateUnit());

        documentBranchRepository.save(documentBranch);

        if (replicate) {
            setupAsyncQueueProducer.sendSetup(new SetupMessage("EXPIRATION_DATE_DOCUMENT_UPDATE",
                    null,
                    null,
                    branchIds,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    Activity.Risk.LOW,
                    ServiceType.Risk.LOW));
        }

        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            if (userResponsible != null) {
                auditLogServiceImpl.createAuditLog(
                        documentBranch.getIdDocumentation(),
                        DOCUMENT,
                        userResponsible.getFullName() + " mudou " + documentBranch.getTitle()
                                + " validade de " + oldAmount + " " + oldUnit
                                + " para " + documentBranch.getExpirationDateAmount() + " " + documentBranch.getExpirationDateUnit(),
                        null,
                        UPDATE,
                        userResponsible.getIdUser());
            }
        }

        return DocumentExpirationResponseDto.builder()
                .idDocument(documentBranch.getIdDocumentation())
                .title(documentBranch.getTitle())
                .expirationDateAmount(documentBranch.getExpirationDateAmount())
                .expirationDateUnit(documentBranch.getExpirationDateUnit())
                .build();
    }
}

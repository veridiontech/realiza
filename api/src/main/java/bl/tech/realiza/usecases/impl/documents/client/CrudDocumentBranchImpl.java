package bl.tech.realiza.usecases.impl.documents.client;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentBranchRepository;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.requests.documents.client.DocumentBranchRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentMatrixResponseDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.gateways.responses.services.DocumentIAValidationResponse;
import bl.tech.realiza.services.documentProcessing.DocumentProcessingService;
import bl.tech.realiza.usecases.interfaces.documents.client.CrudDocumentBranch;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CrudDocumentBranchImpl implements CrudDocumentBranch {

    private final DocumentBranchRepository documentBranchRepository;
    private final DocumentMatrixRepository documentMatrixRepository;
    private final BranchRepository branchRepository;
    private final FileRepository fileRepository;
    private final DocumentProcessingService documentProcessingService;

    @Override
    public DocumentResponseDto save(DocumentBranchRequestDto documentBranchRequestDto, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Invalid file");
        }
        if (documentBranchRequestDto.getBranch() == null || documentBranchRequestDto.getBranch().isEmpty()) {
            throw new BadRequestException("Invalid branch");
        }

        FileDocument fileDocument = null;
        String fileDocumentId = null;
        FileDocument savedFileDocument= null;

        Branch branch = branchRepository.findById(documentBranchRequestDto.getBranch()).orElseThrow(() -> new EntityNotFoundException("Branch not found"));

        DocumentMatrix documentMatrix = documentMatrixRepository.findById(documentBranchRequestDto.getMatrixDocumentId()).orElseThrow(() -> new EntityNotFoundException("Document not found in matrix"));

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
            fileDocumentId = savedFileDocument.getIdDocumentAsString(); // Garante que seja uma String válida
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new EntityNotFoundException(e);
        }

        DocumentBranch newDocumentBranch = DocumentBranch.builder()
                .title(documentMatrix.getName())
                .status(documentBranchRequestDto.getStatus())
                .documentation(fileDocumentId)
                .branch(branch)
                .build();

        DocumentBranch savedDocumentBranch = documentBranchRepository.save(newDocumentBranch);

        DocumentResponseDto documentBranchResponse = DocumentResponseDto.builder()
                .idDocumentation(savedDocumentBranch.getIdDocumentation())
                .title(savedDocumentBranch.getTitle())
                .status(savedDocumentBranch.getStatus())
                .documentation(savedDocumentBranch.getDocumentation())
                .creationDate(savedDocumentBranch.getCreationDate())
                .branch(savedDocumentBranch.getBranch().getIdBranch())
                .build();

        return documentBranchResponse;
    }

    @Override
    public Optional<DocumentResponseDto> findOne(String id) {
        Optional<DocumentBranch> documentBranchOptional = documentBranchRepository.findById(id);

        DocumentBranch documentBranch = documentBranchOptional.orElseThrow(() -> new EntityNotFoundException("DocumentBranch not found"));

        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentBranch.getDocumentation()));

        FileDocument fileDocument = fileDocumentOptional.orElseThrow(() -> new EntityNotFoundException("FileDocument not found"));

        DocumentResponseDto documentBranchResponse = DocumentResponseDto.builder()
                .idDocumentation(documentBranch.getIdDocumentation())
                .title(documentBranch.getTitle())
                .status(documentBranch.getStatus())
                .documentation(documentBranch.getDocumentation())
                .fileName(fileDocument.getName())
                .fileContentType(fileDocument.getContentType())
                .fileData(fileDocument.getData())
                .creationDate(documentBranch.getCreationDate())
                .branch(documentBranch.getBranch().getIdBranch())
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
                            .idDocumentation(documentBranch.getIdDocumentation())
                            .title(documentBranch.getTitle())
                            .status(documentBranch.getStatus())
                            .documentation(documentBranch.getDocumentation())
                            .fileName(fileDocument != null ? fileDocument.getName() : null)
                            .fileContentType(fileDocument != null ? fileDocument.getContentType() : null)
                            .fileData(fileDocument != null ? fileDocument.getData() : null)
                            .creationDate(documentBranch.getCreationDate())
                            .branch(documentBranch.getBranch().getIdBranch())
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

        Optional<DocumentBranch> documentBranchOptional = documentBranchRepository.findById(id);

        DocumentBranch documentBranch = documentBranchOptional.orElseThrow(() -> new EntityNotFoundException("DocumentBranch not found"));

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

        documentBranch.setStatus(documentBranchRequestDto.getStatus() != null ? documentBranchRequestDto.getStatus() : documentBranch.getStatus());

        DocumentBranch savedDocumentBranch = documentBranchRepository.save(documentBranch);

        DocumentResponseDto documentBranchResponse = DocumentResponseDto.builder()
                .idDocumentation(savedDocumentBranch.getIdDocumentation())
                .title(savedDocumentBranch.getTitle())
                .status(savedDocumentBranch.getStatus())
                .documentation(savedDocumentBranch.getDocumentation())
                .creationDate(savedDocumentBranch.getCreationDate())
                .branch(savedDocumentBranch.getBranch().getIdBranch())
                .build();

        return Optional.of(documentBranchResponse);
    }

    @Override
    public void delete(String id) {
        documentBranchRepository.deleteById(id);
    }

    @Override
    public Optional<DocumentResponseDto> upload(String id, MultipartFile file) throws IOException {
        FileDocument fileDocument = null;
        String fileDocumentId = null;
        FileDocument savedFileDocument= null;

        Optional<DocumentBranch> documentBranchOptional = documentBranchRepository.findById(id);

        DocumentBranch documentBranch = documentBranchOptional.orElseThrow(() -> new EntityNotFoundException("DocumentBranch not found"));

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

        DocumentIAValidationResponse documentIAValidation = documentProcessingService.processDocument(file);

        if (documentIAValidation.isAutoValidate()) {
            if (documentIAValidation.isValid()) {
                documentBranch.setStatus(Document.Status.APROVADO);
            } else {
                documentBranch.setStatus(Document.Status.REPROVADO);
            }
        } else {
            documentBranch.setStatus(Document.Status.EM_ANALISE);
        }

        documentBranch.setVersionDate(LocalDateTime.now());

        DocumentBranch savedDocumentBranch = documentBranchRepository.save(documentBranch);

        DocumentResponseDto documentBranchResponse = DocumentResponseDto.builder()
                .idDocumentation(savedDocumentBranch.getIdDocumentation())
                .title(savedDocumentBranch.getTitle())
                .status(savedDocumentBranch.getStatus())
                .documentation(savedDocumentBranch.getDocumentation())
                .creationDate(savedDocumentBranch.getCreationDate())
                .branch(savedDocumentBranch.getBranch().getIdBranch())
                .documentIAValidationResponse(documentIAValidation)
                .build();

        return Optional.of(documentBranchResponse);
    }

    @Override
    public Page<DocumentResponseDto> findAllByBranch(String idSearch, Pageable pageable) {
        Page<DocumentBranch> documentBranchPage = documentBranchRepository.findAllByBranch_IdBranch(idSearch, pageable);

        Page<DocumentResponseDto> documentBranchResponseDtoPage = documentBranchPage.map(
                documentBranch -> {
                    FileDocument fileDocument = null;
                    if (documentBranch.getDocumentation() != null && ObjectId.isValid(documentBranch.getDocumentation())) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentBranch.getDocumentation()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }

                    return DocumentResponseDto.builder()
                            .idDocumentation(documentBranch.getIdDocumentation())
                            .title(documentBranch.getTitle())
                            .status(documentBranch.getStatus())
                            .documentation(documentBranch.getDocumentation())
                            .fileName(fileDocument != null ? fileDocument.getName() : null)
                            .fileContentType(fileDocument != null ? fileDocument.getContentType() : null)
                            .fileData(fileDocument != null ? fileDocument.getData() : null)
                            .creationDate(documentBranch.getCreationDate())
                            .branch(documentBranch.getBranch().getIdBranch())
                            .build();
                }

        );

        return documentBranchResponseDtoPage;
    }

    @Override
    public DocumentResponseDto findAllSelectedDocuments(String id) {
        branchRepository.findById(id).orElseThrow(() -> new NotFoundException("Branch not found"));

        Comparator<DocumentMatrix> byName = Comparator.comparing(DocumentMatrix::getName);

        List<DocumentBranch> documentBranch = documentBranchRepository.findAllByBranch_IdBranch(id);

        List<DocumentMatrixResponseDto> selectedDocumentsEnterprise = documentBranch.stream()
                .filter(doc -> "Documento empresa".equals(doc.getDocumentMatrix().getSubGroup().getGroup().getGroupName()))
                .sorted(Comparator.comparing(db -> db.getDocumentMatrix().getName()))
                .map(doc -> DocumentMatrixResponseDto.builder()
                        .documentId(doc.getIdDocumentation()) // ID do DocumentBranch
                        .idDocumentMatrix(doc.getDocumentMatrix().getIdDocument())
                        .name(doc.getTitle())
                        .idDocumentSubgroup(doc.getDocumentMatrix().getSubGroup().getIdDocumentSubgroup()) // Substitua pelos getters corretos
                        .subgroupName(doc.getDocumentMatrix().getSubGroup().getSubgroupName())
                        .idDocumentGroup(doc.getDocumentMatrix().getSubGroup().getGroup().getIdDocumentGroup())
                        .groupName(doc.getDocumentMatrix().getSubGroup().getGroup().getGroupName())
                        .build())
                .collect(Collectors.toList());
        List<DocumentMatrixResponseDto> selectedDocumentsPersonal = documentBranch.stream()
                .filter(doc -> "Documento pessoa".equals(doc.getDocumentMatrix().getSubGroup().getGroup().getGroupName()))
                .sorted(Comparator.comparing(db -> db.getDocumentMatrix().getName()))
                .map(doc -> DocumentMatrixResponseDto.builder()
                        .documentId(doc.getIdDocumentation()) // ID do DocumentBranch
                        .idDocumentMatrix(doc.getDocumentMatrix().getIdDocument())
                        .name(doc.getTitle())
                        .idDocumentSubgroup(doc.getDocumentMatrix().getSubGroup().getIdDocumentSubgroup()) // Substitua pelos getters corretos
                        .subgroupName(doc.getDocumentMatrix().getSubGroup().getSubgroupName())
                        .idDocumentGroup(doc.getDocumentMatrix().getSubGroup().getGroup().getIdDocumentGroup())
                        .groupName(doc.getDocumentMatrix().getSubGroup().getGroup().getGroupName())
                        .build())
                .collect(Collectors.toList());
        List<DocumentMatrixResponseDto> selectedDocumentsService = documentBranch.stream()
                .filter(doc -> "Documentos empresa-serviço".equals(doc.getDocumentMatrix().getSubGroup().getGroup().getGroupName()))
                .sorted(Comparator.comparing(db -> db.getDocumentMatrix().getName()))
                .map(doc -> DocumentMatrixResponseDto.builder()
                        .documentId(doc.getIdDocumentation()) // ID do DocumentBranch
                        .idDocumentMatrix(doc.getDocumentMatrix().getIdDocument())
                        .name(doc.getTitle())
                        .idDocumentSubgroup(doc.getDocumentMatrix().getSubGroup().getIdDocumentSubgroup()) // Substitua pelos getters corretos
                        .subgroupName(doc.getDocumentMatrix().getSubGroup().getSubgroupName())
                        .idDocumentGroup(doc.getDocumentMatrix().getSubGroup().getGroup().getIdDocumentGroup())
                        .groupName(doc.getDocumentMatrix().getSubGroup().getGroup().getGroupName())
                        .build())
                .collect(Collectors.toList());
        List<DocumentMatrixResponseDto> selectedDocumentsTrainning = documentBranch.stream()
                .filter(doc -> "Treinamentos e certificações".equals(doc.getDocumentMatrix().getSubGroup().getGroup().getGroupName()))
                .sorted(Comparator.comparing(db -> db.getDocumentMatrix().getName()))
                .map(doc -> DocumentMatrixResponseDto.builder()
                        .documentId(doc.getIdDocumentation()) // ID do DocumentBranch
                        .idDocumentMatrix(doc.getDocumentMatrix().getIdDocument())
                        .name(doc.getTitle())
                        .idDocumentSubgroup(doc.getDocumentMatrix().getSubGroup().getIdDocumentSubgroup()) // Substitua pelos getters corretos
                        .subgroupName(doc.getDocumentMatrix().getSubGroup().getSubgroupName())
                        .idDocumentGroup(doc.getDocumentMatrix().getSubGroup().getGroup().getIdDocumentGroup())
                        .groupName(doc.getDocumentMatrix().getSubGroup().getGroup().getGroupName())
                        .build())
                .collect(Collectors.toList());

        List<DocumentMatrixResponseDto> allDocumentsEnterprise = documentMatrixRepository.findAllBySubGroup_Group_GroupName("Documento empresa")
                .stream()
                .sorted(Comparator.comparing(DocumentMatrix::getName))
                .map(doc -> DocumentMatrixResponseDto.builder()
                        .idDocumentMatrix(doc.getIdDocument())
                        .name(doc.getName())
                        .idDocumentSubgroup(doc.getSubGroup().getIdDocumentSubgroup())
                        .subgroupName(doc.getSubGroup().getSubgroupName())
                        .idDocumentGroup(doc.getSubGroup().getGroup().getIdDocumentGroup())
                        .groupName(doc.getSubGroup().getGroup().getGroupName())
                        .build())
                .toList();
        List<DocumentMatrixResponseDto> allDocumentsPersonal = documentMatrixRepository.findAllBySubGroup_Group_GroupName("Documento pessoa")
                .stream()
                .sorted(Comparator.comparing(DocumentMatrix::getName))
                .map(doc -> DocumentMatrixResponseDto.builder()
                        .idDocumentMatrix(doc.getIdDocument())
                        .name(doc.getName())
                        .idDocumentSubgroup(doc.getSubGroup().getIdDocumentSubgroup())
                        .subgroupName(doc.getSubGroup().getSubgroupName())
                        .idDocumentGroup(doc.getSubGroup().getGroup().getIdDocumentGroup())
                        .groupName(doc.getSubGroup().getGroup().getGroupName())
                        .build())
                .toList();
        List<DocumentMatrixResponseDto> allDocumentsService = documentMatrixRepository.findAllBySubGroup_Group_GroupName("Documentos empresa-serviço")
                .stream()
                .sorted(Comparator.comparing(DocumentMatrix::getName))
                .map(doc -> DocumentMatrixResponseDto.builder()
                        .idDocumentMatrix(doc.getIdDocument())
                        .name(doc.getName())
                        .idDocumentSubgroup(doc.getSubGroup().getIdDocumentSubgroup())
                        .subgroupName(doc.getSubGroup().getSubgroupName())
                        .idDocumentGroup(doc.getSubGroup().getGroup().getIdDocumentGroup())
                        .groupName(doc.getSubGroup().getGroup().getGroupName())
                        .build())
                .toList();
        List<DocumentMatrixResponseDto> allDocumentsTrainning = documentMatrixRepository.findAllBySubGroup_Group_GroupName("Treinamentos e certificações")
                .stream()
                .sorted(Comparator.comparing(DocumentMatrix::getName))
                .map(doc -> DocumentMatrixResponseDto.builder()
                        .idDocumentMatrix(doc.getIdDocument())
                        .name(doc.getName())
                        .idDocumentSubgroup(doc.getSubGroup().getIdDocumentSubgroup())
                        .subgroupName(doc.getSubGroup().getSubgroupName())
                        .idDocumentGroup(doc.getSubGroup().getGroup().getIdDocumentGroup())
                        .groupName(doc.getSubGroup().getGroup().getGroupName())
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

        Set<String> selectedDocumentTrainningIds = selectedDocumentsTrainning.stream()
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

        List<DocumentMatrixResponseDto> nonSelectedDocumentsTrainning = allDocumentsTrainning.stream()
                .filter(doc -> !selectedDocumentTrainningIds.contains(doc.getIdDocumentMatrix()))
                .collect(Collectors.toList());

        return DocumentResponseDto.builder()
                .selectedDocumentsEnterprise(selectedDocumentsEnterprise)
                .nonSelectedDocumentsEnterprise(nonSelectedDocumentsEnterprise)
                .selectedDocumentsPersonal(selectedDocumentsPersonal)
                .nonSelectedDocumentsPersonal(nonSelectedDocumentsPersonal)
                .selectedDocumentsService(selectedDocumentsService)
                .nonSelectedDocumentsService(nonSelectedDocumentsService)
                .selectedDocumentsTrainning(selectedDocumentsTrainning)
                .nonSelectedDocumentsTrainning(nonSelectedDocumentsTrainning)
                .build();
    }

    @Override
    public String updateRequiredDocumentsByList(String id, List<String> documentCollection) {
        if (documentCollection == null || documentCollection.isEmpty()) {
            throw new BadRequestException("Invalid documents");
        }

        Branch branch = branchRepository.findById(id).orElseThrow(() -> new NotFoundException("Branch not found"));

        List<DocumentMatrix> documentMatrixList = documentMatrixRepository.findAllById(documentCollection);
        if (documentMatrixList.isEmpty()) {
            throw new NotFoundException("Documents not found");
        }

        List<DocumentBranch> existingDocumentBranches = documentBranchRepository.findAllByBranch_IdBranch(id);

        Set<DocumentMatrix> existingDocuments = existingDocumentBranches.stream()
                .map(DocumentBranch::getDocumentMatrix)
                .collect(Collectors.toSet());

        List<DocumentBranch> newDocumentBranches = documentMatrixList.stream()
                .filter(doc -> !existingDocuments.contains(doc))
                .map(doc -> DocumentBranch.builder()
                        .title(doc.getName())
                        .status(Document.Status.PENDENTE)
                        .branch(branch)
                        .documentMatrix(doc)
                        .build())
                .collect(Collectors.toList());

        List<DocumentBranch> documentsToRemove = existingDocumentBranches.stream()
                .filter(db -> !documentMatrixList.contains(db.getDocumentMatrix()))
                .collect(Collectors.toList());

        if (!documentsToRemove.isEmpty()) {
            documentBranchRepository.deleteAll(documentsToRemove);
        }

        if (!newDocumentBranches.isEmpty()) {
            documentBranchRepository.saveAll(newDocumentBranches);
        }

        return "Documents updated successfully";
    }

    @Override
    public String addRequiredDocument(String idEnterprise, String documentMatrixId) {
        if (documentMatrixId == null || documentMatrixId.isEmpty()) {
            throw new BadRequestException("Invalid documents");
        }

        Branch branch = branchRepository.findById(idEnterprise).orElseThrow(() -> new NotFoundException("Branch not found"));

        DocumentMatrix documentMatrix = documentMatrixRepository.findById(documentMatrixId).orElseThrow(() -> new NotFoundException("Document not found in matrix"));

        DocumentBranch newDocumentBranch = DocumentBranch.builder()
                        .title(documentMatrix.getName())
                        .status(Document.Status.PENDENTE)
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
    public String updateSelectedDocuments(String id, List<DocumentBranch> documentCollection) {
        branchRepository.findById(id).orElseThrow(() -> new NotFoundException("Branch not found"));
        List<DocumentBranch> documentBranches = documentBranchRepository.findAllByBranch_IdBranch(id);

        Map<String,DocumentBranch> existingMap = documentBranches.stream()
                .collect(Collectors.toMap(Document::getIdDocumentation, doc -> doc));

        List<DocumentBranch> toUpdateOrInsert = new ArrayList<>();

        for (DocumentBranch newDoc : documentCollection) {
            if (existingMap.containsKey(newDoc.getIdDocumentation())) {
                DocumentBranch existingDoc = existingMap.get(newDoc.getIdDocumentation());
                // low
                existingDoc.setLowLessThan8h(newDoc.getLowLessThan8h());
                existingDoc.setLowLessThan1m(newDoc.getLowLessThan1m());
                existingDoc.setLowLessThan6m(newDoc.getLowLessThan6m());
                existingDoc.setLowMoreThan6m(newDoc.getLowMoreThan6m());
                // medium
                existingDoc.setMediumLessThan1m(newDoc.getMediumLessThan1m());
                existingDoc.setMediumLessThan6m(newDoc.getMediumLessThan6m());
                existingDoc.setMediumMoreThan6m(newDoc.getMediumMoreThan6m());
                // high
                existingDoc.setHighLessThan1m(newDoc.getHighLessThan1m());
                existingDoc.setHighLessThan6m(newDoc.getHighLessThan6m());
                existingDoc.setHighMoreThan6m(newDoc.getHighMoreThan6m());

                toUpdateOrInsert.add(existingDoc);
            }
        }

        try {
            documentBranchRepository.saveAll(toUpdateOrInsert);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Documents risks updated successfully";
    }

    @Override
    public List<DocumentResponseDto> findAllSelectedDocumentsByRisk(String id, Document.Risk risk) {
        List<DocumentBranch> documentBranchList = List.of();
        List<DocumentResponseDto> documentResponseDto = List.of();
        switch (risk) {
            case LOW_LESS_THAN_8H -> {
                documentBranchList = documentBranchRepository.findAllByBranch_IdBranchAndLowLessThan8hIsTrue(id);
                documentResponseDto = documentBranchList.stream().map(
                        documentBranch -> DocumentResponseDto.builder()
                                .title(documentBranch.getTitle())
                                .idDocumentation(documentBranch.getIdDocumentation())
                                .build()
                ).toList();
                return documentResponseDto;
            }
            case LOW_LESS_THAN_1M -> {
                documentBranchList = documentBranchRepository.findAllByBranch_IdBranchAndLowLessThan1mIsTrue(id);
                documentResponseDto = documentBranchList.stream().map(
                        documentBranch -> DocumentResponseDto.builder()
                                .title(documentBranch.getTitle())
                                .idDocumentation(documentBranch.getIdDocumentation())
                                .build()
                ).toList();
                return documentResponseDto;
            }
            case LOW_LESS_THAN_6M -> {
                documentBranchList = documentBranchRepository.findAllByBranch_IdBranchAndLowLessThan6mIsTrue(id);
                documentResponseDto = documentBranchList.stream().map(
                        documentBranch -> DocumentResponseDto.builder()
                                .title(documentBranch.getTitle())
                                .idDocumentation(documentBranch.getIdDocumentation())
                                .build()
                ).toList();
                return documentResponseDto;
            }
            case LOW_MORE_THAN_6M -> {
                documentBranchList = documentBranchRepository.findAllByBranch_IdBranchAndLowMoreThan6mIsTrue(id);
                documentResponseDto = documentBranchList.stream().map(
                        documentBranch -> DocumentResponseDto.builder()
                                .title(documentBranch.getTitle())
                                .idDocumentation(documentBranch.getIdDocumentation())
                                .build()
                ).toList();
                return documentResponseDto;
            }
            case MEDIUM_LESS_THAN_1M -> {
                documentBranchList = documentBranchRepository.findAllByBranch_IdBranchAndMediumLessThan1mIsTrue(id);
                documentResponseDto = documentBranchList.stream().map(
                        documentBranch -> DocumentResponseDto.builder()
                                .title(documentBranch.getTitle())
                                .idDocumentation(documentBranch.getIdDocumentation())
                                .build()
                ).toList();
                return documentResponseDto;
            }
            case MEDIUM_LESS_THAN_6M -> {
                documentBranchList = documentBranchRepository.findAllByBranch_IdBranchAndMediumLessThan6mIsTrue(id);
                documentResponseDto = documentBranchList.stream().map(
                        documentBranch -> DocumentResponseDto.builder()
                                .title(documentBranch.getTitle())
                                .idDocumentation(documentBranch.getIdDocumentation())
                                .build()
                ).toList();
                return documentResponseDto;
            }
            case MEDIUM_MORE_THAN_6M -> {
                documentBranchList = documentBranchRepository.findAllByBranch_IdBranchAndMediumMoreThan6mIsTrue(id);
                documentResponseDto = documentBranchList.stream().map(
                        documentBranch -> DocumentResponseDto.builder()
                                .title(documentBranch.getTitle())
                                .idDocumentation(documentBranch.getIdDocumentation())
                                .build()
                ).toList();
                return documentResponseDto;
            }
            case HIGH_LESS_THAN_1M -> {
                documentBranchList = documentBranchRepository.findAllByBranch_IdBranchAndHighLessThan1mIsTrue(id);
                documentResponseDto = documentBranchList.stream().map(
                        documentBranch -> DocumentResponseDto.builder()
                                .title(documentBranch.getTitle())
                                .idDocumentation(documentBranch.getIdDocumentation())
                                .build()
                ).toList();
                return documentResponseDto;
            }
            case HIGH_LESS_THAN_6M -> {
                documentBranchList = documentBranchRepository.findAllByBranch_IdBranchAndHighLessThan6mIsTrue(id);
                documentResponseDto = documentBranchList.stream().map(
                        documentBranch -> DocumentResponseDto.builder()
                                .title(documentBranch.getTitle())
                                .idDocumentation(documentBranch.getIdDocumentation())
                                .build()
                ).toList();
                return documentResponseDto;
            }
            case HIGH_MORE_THAN_6M -> {
                documentBranchList = documentBranchRepository.findAllByBranch_IdBranchAndHighMoreThan6mIsTrue(id);
                documentResponseDto = documentBranchList.stream().map(
                        documentBranch -> DocumentResponseDto.builder()
                                .title(documentBranch.getTitle())
                                .idDocumentation(documentBranch.getIdDocumentation())
                                .build()
                ).toList();
                return documentResponseDto;
            }
            default -> {
                throw new BadRequestException("Invalid risk");
            }
        }
    }
}

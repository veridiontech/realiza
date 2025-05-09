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
import bl.tech.realiza.gateways.responses.documents.DocumentExpirationResponseDto;
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
    public Optional<DocumentResponseDto> findOne(String id) {
        Optional<DocumentBranch> documentBranchOptional = documentBranchRepository.findById(id);

        DocumentBranch documentBranch = documentBranchOptional.orElseThrow(() -> new EntityNotFoundException("DocumentBranch not found"));

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
                            .idDocument(documentBranch.getIdDocumentation())
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
                .idDocument(savedDocumentBranch.getIdDocumentation())
                .title(savedDocumentBranch.getTitle())
                .status(savedDocumentBranch.getStatus())
                .documentation(savedDocumentBranch.getDocumentation())
                .creationDate(savedDocumentBranch.getCreationDate())
                .branch(savedDocumentBranch.getBranch().getIdBranch())
                .build();

        return Optional.of(documentBranchResponse);
    }

    @Override
    public Optional<DocumentResponseDto> upload(String id, MultipartFile file) throws IOException {
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
                        .ownerId(documentBranch.getBranch().getIdBranch())
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
                documentBranch.setStatus(Document.Status.APROVADO_IA);
            } else {
                documentBranch.setStatus(Document.Status.REPROVADO_IA);
            }
        } else {
            documentBranch.setStatus(Document.Status.EM_ANALISE);
        }

        documentBranch.setVersionDate(LocalDateTime.now());

        DocumentBranch savedDocumentBranch = documentBranchRepository.save(documentBranch);

        DocumentResponseDto documentBranchResponse = DocumentResponseDto.builder()
                .idDocument(savedDocumentBranch.getIdDocumentation())
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

        List<DocumentBranch> documentBranch = documentBranchRepository.findAllByBranch_IdBranchAndIsActiveIsTrue(id);

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
        List<DocumentMatrixResponseDto> selectedDocumentsTraining = documentBranch.stream()
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
        List<DocumentMatrixResponseDto> allDocumentsTraining = documentMatrixRepository.findAllBySubGroup_Group_GroupName("Treinamentos e certificações")
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
    public List<DocumentResponseDto> findAllFilteredDocuments(String id, String documentTypeName, Boolean isSelected) {
        branchRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Branch not found"));

        List<DocumentBranch> documentBranch = documentBranchRepository
                .findAllByBranch_IdBranchAndDocumentMatrix_TypeAndIsActive(id, documentTypeName.toLowerCase(), isSelected);

        return documentBranch.stream()
                .sorted(Comparator.comparing(document -> document.getDocumentMatrix().getName()))
                .map(document -> DocumentResponseDto.builder()
                        .idDocument(document.getIdDocumentation())
                        .title(document.getTitle())
                        .build()).toList();
    }

    @Override
    public List<DocumentExpirationResponseDto> findAllFilteredDocumentsExpiration(String idBranch, String documentTypeName, Boolean isSelected) {
        branchRepository.findById(idBranch)
                .orElseThrow(() -> new NotFoundException("Branch not found"));

        List<DocumentBranch> documentBranch = documentBranchRepository
                .findAllByBranch_IdBranchAndDocumentMatrix_TypeAndIsActive(idBranch, documentTypeName.toLowerCase(), isSelected);

        return documentBranch.stream()
                .sorted(Comparator.comparing(document -> document.getDocumentMatrix().getName()))
                .map(document -> DocumentExpirationResponseDto.builder()
                        .idDocument(document.getIdDocumentation())
                        .title(document.getTitle())
                        .expirationDateAmount(document.getExpirationDateAmount())
                        .expirationDateUnit(document.getExpirationDateUnit())
                        .build()).toList();
    }

    @Override
    public String updateSelectedDocuments(Boolean isSelected, List<String> documentCollection) {
        if (documentCollection == null || documentCollection.isEmpty()) {
            throw new BadRequestException("Invalid documents");
        }

        List<DocumentBranch> documentList = documentBranchRepository.findAllById(documentCollection);

        if (documentList.isEmpty()) {
            throw new NotFoundException("Documents not found");
        }

        documentList.forEach(documentBranch -> documentBranch.setIsActive(isSelected));

        documentBranchRepository.saveAll(documentList);

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
    public DocumentExpirationResponseDto updateSelectedDocumentExpiration() {
        return null;
    }
}

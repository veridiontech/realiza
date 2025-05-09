package bl.tech.realiza.usecases.impl.documents.provider;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.requests.documents.provider.DocumentProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentMatrixResponseDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.gateways.responses.services.DocumentIAValidationResponse;
import bl.tech.realiza.services.documentProcessing.DocumentProcessingService;
import bl.tech.realiza.usecases.interfaces.documents.provider.CrudDocumentProviderSubcontractor;
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
public class CrudDocumentProviderSubcontractorImpl implements CrudDocumentProviderSubcontractor {

    private final DocumentProviderSubcontractorRepository documentSubcontractorRepository;
    private final ProviderSubcontractorRepository providerSubcontractorRepository;
    private final FileRepository fileRepository;
    private final DocumentProviderSubcontractorRepository documentProviderSubcontractorRepository;
    private final DocumentMatrixRepository documentMatrixRepository;
    private final DocumentProcessingService documentProcessingService;

    @Override
    public DocumentResponseDto save(DocumentProviderSubcontractorRequestDto documentProviderSubcontractorRequestDto, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Invalid file");
        }
        if (documentProviderSubcontractorRequestDto.getSubcontractor() == null || documentProviderSubcontractorRequestDto.getSubcontractor().isEmpty()) {
            throw new BadRequestException("Invalid subcontractor");
        }

        FileDocument fileDocument = null;
        String fileDocumentId = null;

        Optional<ProviderSubcontractor> providerSubcontractorOptional = providerSubcontractorRepository.findById(documentProviderSubcontractorRequestDto.getSubcontractor());

        ProviderSubcontractor providerSubcontractor = providerSubcontractorOptional.orElseThrow(() -> new EntityNotFoundException("Subcontractor not found"));

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

        FileDocument savedFileDocument= null;
        try {
            savedFileDocument = fileRepository.save(fileDocument);
            fileDocumentId = savedFileDocument.getIdDocumentAsString(); // Garante que seja uma String válida
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new EntityNotFoundException(e);
        }

        DocumentProviderSubcontractor newDocumentSubcontractor = DocumentProviderSubcontractor.builder()
                .title(documentProviderSubcontractorRequestDto.getTitle())
                .status(documentProviderSubcontractorRequestDto.getStatus())
                .documentation(fileDocumentId)
                .providerSubcontractor(providerSubcontractor)
                .build();

        DocumentProviderSubcontractor savedDocumentSubcontractor = documentSubcontractorRepository.save(newDocumentSubcontractor);

        DocumentResponseDto documentSubcontractorResponse = DocumentResponseDto.builder()
                .idDocument(savedDocumentSubcontractor.getDocumentation())
                .title(savedDocumentSubcontractor.getTitle())
                .status(savedDocumentSubcontractor.getStatus())
                .documentation(savedDocumentSubcontractor.getDocumentation())
                .creationDate(savedDocumentSubcontractor.getCreationDate())
                .subcontractor(savedDocumentSubcontractor.getProviderSubcontractor().getIdProvider())
                .build();

        return documentSubcontractorResponse;
    }

    @Override
    public Optional<DocumentResponseDto> findOne(String id) {
        Optional<DocumentProviderSubcontractor> documentSubcontractorOptional = documentSubcontractorRepository.findById(id);

        DocumentProviderSubcontractor documentSubcontractor = documentSubcontractorOptional.orElseThrow(() -> new EntityNotFoundException("Subcontractor not found"));

        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentSubcontractor.getDocumentation()));
        FileDocument fileDocument = fileDocumentOptional.orElseThrow(() -> new EntityNotFoundException("FileDocument not found"));

        DocumentResponseDto documentSubcontractorResponse = DocumentResponseDto.builder()
                .idDocument(documentSubcontractor.getDocumentation())
                .title(documentSubcontractor.getTitle())
                .status(documentSubcontractor.getStatus())
                .documentation(documentSubcontractor.getDocumentation())
                .fileName(fileDocument.getName())
                .fileContentType(fileDocument.getContentType())
                .fileData(fileDocument.getData())
                .creationDate(documentSubcontractor.getCreationDate())
                .subcontractor(documentSubcontractor.getProviderSubcontractor().getIdProvider())
                .build();

        return Optional.of(documentSubcontractorResponse);
    }

    @Override
    public Page<DocumentResponseDto> findAll(Pageable pageable) {
        Page<DocumentProviderSubcontractor> documentSubcontractorPage = documentSubcontractorRepository.findAll(pageable);

        Page<DocumentResponseDto> documentSubcontractorResponseDtoPage = documentSubcontractorPage.map(
                documentSubcontractor -> {
                    FileDocument fileDocument = null;
                    if (documentSubcontractor.getDocumentation() != null && ObjectId.isValid(documentSubcontractor.getDocumentation())) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentSubcontractor.getDocumentation()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }

                    return DocumentResponseDto.builder()
                            .idDocument(documentSubcontractor.getDocumentation())
                            .title(documentSubcontractor.getTitle())
                            .status(documentSubcontractor.getStatus())
                            .documentation(documentSubcontractor.getDocumentation())
                            .fileName(fileDocument != null ? fileDocument.getName() : null)
                            .fileContentType(fileDocument != null ? fileDocument.getContentType() : null)
                            .fileData(fileDocument != null ? fileDocument.getData() : null)
                            .creationDate(documentSubcontractor.getCreationDate())
                            .subcontractor(documentSubcontractor.getProviderSubcontractor().getIdProvider())
                            .build();
                }
        );

        return documentSubcontractorResponseDtoPage;
    }

    @Override
    public Optional<DocumentResponseDto> update(String id, DocumentProviderSubcontractorRequestDto documentProviderSubcontractorRequestDto, MultipartFile file) throws IOException {
        FileDocument fileDocument = null;
        String fileDocumentId = null;
        FileDocument savedFileDocument= null;

        Optional<DocumentProviderSubcontractor> documentSubcontractorOptional = documentSubcontractorRepository.findById(id);

        DocumentProviderSubcontractor documentSubcontractor = documentSubcontractorOptional.orElseThrow(() -> new EntityNotFoundException("Subcontractor not found"));

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
            documentSubcontractor.setDocumentation(fileDocumentId);
        }

        documentSubcontractor.setStatus(documentProviderSubcontractorRequestDto.getStatus() != null ? documentProviderSubcontractorRequestDto.getStatus() : documentSubcontractor.getStatus());

        DocumentProviderSubcontractor savedDocumentSubcontractor = documentSubcontractorRepository.save(documentSubcontractor);

        DocumentResponseDto documentSubcontractorResponse = DocumentResponseDto.builder()
                .idDocument(savedDocumentSubcontractor.getDocumentation())
                .title(savedDocumentSubcontractor.getTitle())
                .status(savedDocumentSubcontractor.getStatus())
                .documentation(savedDocumentSubcontractor.getDocumentation())
                .creationDate(savedDocumentSubcontractor.getCreationDate())
                .subcontractor(savedDocumentSubcontractor.getProviderSubcontractor().getIdProvider())
                .build();

        return Optional.of(documentSubcontractorResponse);
    }

    @Override
    public void delete(String id) {
        documentSubcontractorRepository.deleteById(id);
    }

    @Override
    public Optional<DocumentResponseDto> upload(String id, MultipartFile file) throws IOException {
        FileDocument fileDocument = null;
        String fileDocumentId = null;
        FileDocument savedFileDocument= null;

        DocumentProviderSubcontractor documentProviderSubcontractor = documentSubcontractorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("DocumentBranch not found"));

        if (file != null && !file.isEmpty()) {
            try {
                fileDocument = FileDocument.builder()
                        .name(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .owner(FileDocument.Owner.SUBCONTRACTOR)
                        .ownerId(documentProviderSubcontractor.getProviderSubcontractor().getIdProvider())
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
            documentProviderSubcontractor.setDocumentation(fileDocumentId);
        }

        DocumentIAValidationResponse documentIAValidation = documentProcessingService.processDocument(file);

        if (documentIAValidation.isAutoValidate()) {
            if (documentIAValidation.isValid()) {
                documentProviderSubcontractor.setStatus(Document.Status.APROVADO_IA);
            } else {
                documentProviderSubcontractor.setStatus(Document.Status.REPROVADO_IA);
            }
        } else {
            documentProviderSubcontractor.setStatus(Document.Status.EM_ANALISE);
        }

        documentProviderSubcontractor.setVersionDate(LocalDateTime.now());

        DocumentProviderSubcontractor savedDocumentSubcontractor = documentSubcontractorRepository.save(documentProviderSubcontractor);

        DocumentResponseDto documentSubcontractorResponse = DocumentResponseDto.builder()
                .idDocument(savedDocumentSubcontractor.getIdDocumentation())
                .title(savedDocumentSubcontractor.getTitle())
                .status(savedDocumentSubcontractor.getStatus())
                .documentation(savedDocumentSubcontractor.getDocumentation())
                .creationDate(savedDocumentSubcontractor.getCreationDate())
                .subcontractor(savedDocumentSubcontractor.getProviderSubcontractor().getIdProvider())
                .documentIAValidationResponse(documentIAValidation)
                .build();

        return Optional.of(documentSubcontractorResponse);
    }

    @Override
    public Page<DocumentResponseDto> findAllBySubcontractor(String idSearch, Pageable pageable) {
        Page<DocumentProviderSubcontractor> documentSubcontractorPage = documentSubcontractorRepository.findAllByProviderSubcontractor_IdProviderAndIsActiveIsTrue(idSearch, pageable);

        Page<DocumentResponseDto> documentSubcontractorResponseDtoPage = documentSubcontractorPage.map(
                documentSubcontractor -> {
                    FileDocument fileDocument = null;
                    if (documentSubcontractor.getDocumentation() != null && ObjectId.isValid(documentSubcontractor.getDocumentation())) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentSubcontractor.getDocumentation()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }

                    return DocumentResponseDto.builder()
                            .idDocument(documentSubcontractor.getDocumentation())
                            .title(documentSubcontractor.getTitle())
                            .status(documentSubcontractor.getStatus())
                            .documentation(documentSubcontractor.getDocumentation())
                            .fileName(fileDocument != null ? fileDocument.getName() : null)
                            .fileContentType(fileDocument != null ? fileDocument.getContentType() : null)
                            .fileData(fileDocument != null ? fileDocument.getData() : null)
                            .creationDate(documentSubcontractor.getCreationDate())
                            .subcontractor(documentSubcontractor.getProviderSubcontractor().getIdProvider())
                            .build();
                }
        );

        return documentSubcontractorResponseDtoPage;
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
                        .idDocumentSubgroup(doc.getDocumentMatrix().getSubGroup().getIdDocumentSubgroup()) // Substitua pelos getters corretos
                        .subgroupName(doc.getDocumentMatrix().getSubGroup().getSubgroupName())
                        .idDocumentGroup(doc.getDocumentMatrix().getSubGroup().getGroup().getIdDocumentGroup())
                        .groupName(doc.getDocumentMatrix().getSubGroup().getGroup().getGroupName())
                        .build())
                .collect(Collectors.toList());
        List<DocumentMatrixResponseDto> allDocuments = documentMatrixRepository.findAllBySubGroup_Group_GroupName("Documentos empresa-serviço")
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
                        .status(Document.Status.PENDENTE)
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
                .status(Document.Status.PENDENTE)
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

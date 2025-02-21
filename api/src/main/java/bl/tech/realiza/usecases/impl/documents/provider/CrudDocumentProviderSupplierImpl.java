package bl.tech.realiza.usecases.impl.documents.provider;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.requests.documents.provider.DocumentProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.gateways.responses.services.DocumentIAValidationResponse;
import bl.tech.realiza.services.documentProcessing.DocumentProcessingService;
import bl.tech.realiza.usecases.interfaces.documents.provider.CrudDocumentProviderSupplier;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CrudDocumentProviderSupplierImpl implements CrudDocumentProviderSupplier {

    private final DocumentProviderSupplierRepository documentSupplierRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final FileRepository fileRepository;
    private final DocumentMatrixRepository documentMatrixRepository;
    private final DocumentProcessingService documentProcessingService;

    @Override
    public DocumentResponseDto save(DocumentProviderSupplierRequestDto documentProviderSupplierRequestDto, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Invalid file");
        }
        if (documentProviderSupplierRequestDto.getSupplier() == null || documentProviderSupplierRequestDto.getSupplier().isEmpty()) {
            throw new BadRequestException("Invalid supplier");
        }

        FileDocument fileDocument = null;
        String fileDocumentId = null;
        FileDocument savedFileDocument = null;

        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(documentProviderSupplierRequestDto.getSupplier());

        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new EntityNotFoundException("Provider supplier not found"));

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

        DocumentProviderSupplier newDocumentSupplier = DocumentProviderSupplier.builder()
                .title(documentProviderSupplierRequestDto.getTitle())
                .status(documentProviderSupplierRequestDto.getStatus())
                .documentation(fileDocumentId)
                .providerSupplier(providerSupplier)
                .build();

        DocumentProviderSupplier savedDocumentSupplier = documentSupplierRepository.save(newDocumentSupplier);

        DocumentResponseDto documentSupplierResponse = DocumentResponseDto.builder()
                .idDocumentation(savedDocumentSupplier.getDocumentation())
                .title(savedDocumentSupplier.getTitle())
                .status(savedDocumentSupplier.getStatus())
                .documentation(savedDocumentSupplier.getDocumentation())
                .creationDate(savedDocumentSupplier.getCreationDate())
                .supplier(savedDocumentSupplier.getProviderSupplier().getIdProvider())
                .build();

        return documentSupplierResponse;
    }

    @Override
    public Optional<DocumentResponseDto> findOne(String id) {
        Optional<DocumentProviderSupplier> documentSupplierOptional = documentSupplierRepository.findById(id);

        DocumentProviderSupplier documentSupplier = documentSupplierOptional.orElseThrow(() -> new EntityNotFoundException("Document supplier not found"));

        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentSupplier.getDocumentation()));
        FileDocument fileDocument = fileDocumentOptional.orElseThrow(() -> new EntityNotFoundException("FileDocument not found"));

        DocumentResponseDto documentSupplierResponse = DocumentResponseDto.builder()
                .idDocumentation(documentSupplier.getDocumentation())
                .title(documentSupplier.getTitle())
                .status(documentSupplier.getStatus())
                .documentation(documentSupplier.getDocumentation())
                .fileName(fileDocument.getName())
                .fileContentType(fileDocument.getContentType())
                .fileData(fileDocument.getData())
                .creationDate(documentSupplier.getCreationDate())
                .supplier(documentSupplier.getProviderSupplier().getIdProvider())
                .build();

        return Optional.of(documentSupplierResponse);
    }

    @Override
    public Page<DocumentResponseDto> findAll(Pageable pageable) {
        Page<DocumentProviderSupplier> documentSupplierPage = documentSupplierRepository.findAll(pageable);

        Page<DocumentResponseDto> documentSupplierResponseDtoPage = documentSupplierPage.map(
                documentSupplier -> {
                    FileDocument fileDocument = null;
                    if (documentSupplier.getDocumentation() != null && ObjectId.isValid(documentSupplier.getDocumentation())) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentSupplier.getDocumentation()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }

                    return DocumentResponseDto.builder()
                            .idDocumentation(documentSupplier.getDocumentation())
                            .title(documentSupplier.getTitle())
                            .status(documentSupplier.getStatus())
                            .documentation(documentSupplier.getDocumentation())
                            .fileName(fileDocument != null ? fileDocument.getName() : null)
                            .fileContentType(fileDocument != null ? fileDocument.getContentType() : null)
                            .fileData(fileDocument != null ? fileDocument.getData() : null)
                            .creationDate(documentSupplier.getCreationDate())
                            .supplier(documentSupplier.getProviderSupplier().getIdProvider())
                            .build();
                }
        );

        return documentSupplierResponseDtoPage;
    }

    @Override
    public Optional<DocumentResponseDto> update(String id, DocumentProviderSupplierRequestDto documentProviderSupplierRequestDto, MultipartFile file) throws IOException {
        FileDocument fileDocument = null;
        String fileDocumentId = null;
        FileDocument savedFileDocument= null;

        Optional<DocumentProviderSupplier> documentSupplierOptional = documentSupplierRepository.findById(id);

        DocumentProviderSupplier documentSupplier = documentSupplierOptional.orElseThrow(() -> new EntityNotFoundException("Document supplier not found"));

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
            documentSupplier.setDocumentation(fileDocumentId);
        }

        documentSupplier.setStatus(documentProviderSupplierRequestDto.getStatus() != null ? documentProviderSupplierRequestDto.getStatus() : documentSupplier.getStatus());

        DocumentProviderSupplier savedDocumentSupplier = documentSupplierRepository.save(documentSupplier);

        DocumentResponseDto documentSupplierResponse = DocumentResponseDto.builder()
                .idDocumentation(savedDocumentSupplier.getDocumentation())
                .title(savedDocumentSupplier.getTitle())
                .status(savedDocumentSupplier.getStatus())
                .documentation(savedDocumentSupplier.getDocumentation())
                .creationDate(savedDocumentSupplier.getCreationDate())
                .supplier(savedDocumentSupplier.getProviderSupplier().getIdProvider())
                .build();

        return Optional.of(documentSupplierResponse);
    }

    @Override
    public void delete(String id) {
        documentSupplierRepository.deleteById(id);
    }

    @Override
    public Optional<DocumentResponseDto> upload(String id, MultipartFile file) throws IOException {
        FileDocument fileDocument = null;
        String fileDocumentId = null;
        FileDocument savedFileDocument= null;

        DocumentProviderSupplier documentSupplier = documentSupplierRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Document supplier not found"));

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
            documentSupplier.setDocumentation(fileDocumentId);
        }

        DocumentIAValidationResponse documentIAValidation = documentProcessingService.processDocument(file);

        if (documentIAValidation.isAutoValidate()) {
            if (documentIAValidation.isValid()) {
                documentSupplier.setStatus(Document.Status.APROVADO);
            } else {
                documentSupplier.setStatus(Document.Status.REPROVADO);
            }
        } else {
            documentSupplier.setStatus(Document.Status.EM_ANALISE);
        }

        documentSupplier.setVersionDate(LocalDateTime.now());

        DocumentProviderSupplier savedDocumentSupplier = documentSupplierRepository.save(documentSupplier);

        DocumentResponseDto documentSupplierResponse = DocumentResponseDto.builder()
                .idDocumentation(savedDocumentSupplier.getIdDocumentation())
                .title(savedDocumentSupplier.getTitle())
                .status(savedDocumentSupplier.getStatus())
                .documentation(savedDocumentSupplier.getDocumentation())
                .creationDate(savedDocumentSupplier.getCreationDate())
                .supplier(savedDocumentSupplier.getProviderSupplier().getIdProvider())
                .documentIAValidationResponse(documentIAValidation)
                .build();

        return Optional.of(documentSupplierResponse);
    }

    @Override
    public Page<DocumentResponseDto> findAllBySupplier(String idSearch, Pageable pageable) {
        Page<DocumentProviderSupplier> documentSupplierPage = documentSupplierRepository.findAllByProviderSupplier_IdProvider(idSearch, pageable);

        Page<DocumentResponseDto> documentSupplierResponseDtoPage = documentSupplierPage.map(
                documentSupplier -> {
                    FileDocument fileDocument = null;
                    if (documentSupplier.getDocumentation() != null && ObjectId.isValid(documentSupplier.getDocumentation())) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentSupplier.getDocumentation()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }

                    return DocumentResponseDto.builder()
                            .idDocumentation(documentSupplier.getDocumentation())
                            .title(documentSupplier.getTitle())
                            .status(documentSupplier.getStatus())
                            .documentation(documentSupplier.getDocumentation())
                            .fileName(fileDocument != null ? fileDocument.getName() : null)
                            .fileContentType(fileDocument != null ? fileDocument.getContentType() : null)
                            .fileData(fileDocument != null ? fileDocument.getData() : null)
                            .creationDate(documentSupplier.getCreationDate())
                            .supplier(documentSupplier.getProviderSupplier().getIdProvider())
                            .build();
                }
        );

        return documentSupplierResponseDtoPage;
    }

    @Override
    public DocumentResponseDto findAllSelectedDocuments(String id) {
        providerSupplierRepository.findById(id).orElseThrow(() -> new NotFoundException("Supplier not found"));
        List<DocumentProviderSupplier> documentSupplier = documentSupplierRepository.findAllByProviderSupplier_IdProvider(id);
        List<DocumentMatrix> selectedDocuments = documentSupplier.stream().map(DocumentProviderSupplier::getDocumentMatrix).collect(Collectors.toList());
        List<DocumentMatrix> allDocuments = documentMatrixRepository.findAllBySubGroup_Group_GroupName("Documentos empresa-serviço");
        List<DocumentMatrix> nonSelectedDocuments = new ArrayList<>(allDocuments);
        nonSelectedDocuments.removeAll(selectedDocuments);
        DocumentResponseDto employeeResponse = DocumentResponseDto.builder()
                .selectedDocumentsEnterprise(selectedDocuments)
                .nonSelectedDocumentsEnterprise(nonSelectedDocuments)
                .build();

        return employeeResponse;
    }

    @Override
    public String updateRequiredDocuments(String id, List<String> documentCollection) {
        if (documentCollection == null || documentCollection.isEmpty()) {
            throw new NotFoundException("Invalid documents");
        }

        ProviderSupplier providerSupplier = providerSupplierRepository.findById(id).orElseThrow(() -> new NotFoundException("Supplier not found"));

        List<DocumentMatrix> documentMatrixList = documentMatrixRepository.findAllById(documentCollection);
        if (documentMatrixList.isEmpty()) {
            throw new NotFoundException("Documents not found");
        }

        List<DocumentProviderSupplier> existingDocumentSuppliers = documentSupplierRepository.findAllByProviderSupplier_IdProvider(id);

        Set<DocumentMatrix> existingDocuments = existingDocumentSuppliers.stream()
                .map(DocumentProviderSupplier::getDocumentMatrix)
                .collect(Collectors.toSet());

        List<DocumentProviderSupplier> newDocumentSuppliers = documentMatrixList.stream()
                .filter(doc -> !existingDocuments.contains(doc))
                .map(doc -> DocumentProviderSupplier.builder()
                        .title(doc.getName())
                        .status(Document.Status.PENDENTE)
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

        ProviderSupplier providerSupplier = providerSupplierRepository.findById(idEnterprise).orElseThrow(() -> new NotFoundException("Supplier not found"));

        DocumentMatrix documentMatrix = documentMatrixRepository.findById(documentMatrixId).orElseThrow(() -> new NotFoundException("Document not found in matrix"));

        List<DocumentProviderSupplier> existingDocumentBranches = documentSupplierRepository.findAllByProviderSupplier_IdProvider(idEnterprise);

        Set<DocumentMatrix> existingDocuments = existingDocumentBranches.stream()
                .map(DocumentProviderSupplier::getDocumentMatrix)
                .collect(Collectors.toSet());

        DocumentProviderSupplier newDocumentBranch = DocumentProviderSupplier.builder()
                .title(documentMatrix.getName())
                .status(Document.Status.PENDENTE)
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

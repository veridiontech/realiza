package bl.tech.realiza.usecases.impl.documents.contract;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.documents.contract.DocumentContract;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixRepository;
import bl.tech.realiza.gateways.repositories.documents.contract.DocumentContractRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.requests.documents.contract.DocumentContractRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentMatrixResponseDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.gateways.responses.services.DocumentIAValidationResponse;
import bl.tech.realiza.services.documentProcessing.DocumentProcessingService;
import bl.tech.realiza.usecases.interfaces.documents.contract.CrudDocumentContract;
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
public class CrudDocumentContractImpl implements CrudDocumentContract {
    private final DocumentContractRepository documentContractRepository;
    private final ContractRepository contractRepository;
    private final FileRepository fileRepository;
    private final DocumentMatrixRepository documentMatrixRepository;
    private final DocumentProcessingService documentProcessingService;

    @Override
    public DocumentResponseDto save(DocumentContractRequestDto documentContractRequestDto, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Invalid file");
        }
        if (documentContractRequestDto.getContract() == null || documentContractRequestDto.getContract().isEmpty()) {
            throw new BadRequestException("Invalid contract");
        }

        FileDocument fileDocument = null;
        String fileDocumentId = null;

        Contract contract = contractRepository.findById(documentContractRequestDto.getContract())
                .orElseThrow(() -> new EntityNotFoundException("Subcontractor not found"));

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

        DocumentContract newDocumentSubcontractor = DocumentContract.builder()
                .title(documentContractRequestDto.getTitle())
                .status(documentContractRequestDto.getStatus())
                .documentation(fileDocumentId)
                .contract(contract)
                .build();

        DocumentContract savedDocumentSubcontractor = documentContractRepository.save(newDocumentSubcontractor);

        DocumentResponseDto documentSubcontractorResponse = DocumentResponseDto.builder()
                .idDocumentation(savedDocumentSubcontractor.getDocumentation())
                .title(savedDocumentSubcontractor.getTitle())
                .status(savedDocumentSubcontractor.getStatus())
                .documentation(savedDocumentSubcontractor.getDocumentation())
                .creationDate(savedDocumentSubcontractor.getCreationDate())
                .contract(savedDocumentSubcontractor.getContract().getIdContract())
                .build();

        return documentSubcontractorResponse;
    }

    @Override
    public Optional<DocumentResponseDto> findOne(String id) {
        Optional<DocumentContract> documentContractOptional = documentContractRepository.findById(id);

        DocumentContract documentContract = documentContractOptional.orElseThrow(() -> new EntityNotFoundException("Subcontractor not found"));

        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentContract.getDocumentation()));
        FileDocument fileDocument = fileDocumentOptional.orElseThrow(() -> new EntityNotFoundException("FileDocument not found"));

        DocumentResponseDto documentSubcontractorResponse = DocumentResponseDto.builder()
                .idDocumentation(documentContract.getDocumentation())
                .title(documentContract.getTitle())
                .status(documentContract.getStatus())
                .documentation(documentContract.getDocumentation())
                .fileName(fileDocument.getName())
                .fileContentType(fileDocument.getContentType())
                .fileData(fileDocument.getData())
                .creationDate(documentContract.getCreationDate())
                .contract(documentContract.getContract().getIdContract())
                .build();

        return Optional.of(documentSubcontractorResponse);
    }

    @Override
    public Page<DocumentResponseDto> findAll(Pageable pageable) {
        Page<DocumentContract> documentSubcontractorPage = documentContractRepository.findAll(pageable);

        Page<DocumentResponseDto> documentSubcontractorResponseDtoPage = documentSubcontractorPage.map(
                documentContract -> {
                    FileDocument fileDocument = null;
                    if (documentContract.getDocumentation() != null && ObjectId.isValid(documentContract.getDocumentation())) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentContract.getDocumentation()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }

                    return DocumentResponseDto.builder()
                            .idDocumentation(documentContract.getDocumentation())
                            .title(documentContract.getTitle())
                            .status(documentContract.getStatus())
                            .documentation(documentContract.getDocumentation())
                            .fileName(fileDocument != null ? fileDocument.getName() : null)
                            .fileContentType(fileDocument != null ? fileDocument.getContentType() : null)
                            .fileData(fileDocument != null ? fileDocument.getData() : null)
                            .creationDate(documentContract.getCreationDate())
                            .contract(documentContract.getContract().getIdContract())
                            .build();
                }
        );

        return documentSubcontractorResponseDtoPage;
    }

    @Override
    public Optional<DocumentResponseDto> update(String id, DocumentContractRequestDto documentContractRequestDto, MultipartFile file) throws IOException {
        FileDocument fileDocument = null;
        String fileDocumentId = null;
        FileDocument savedFileDocument= null;

        DocumentContract documentContract = documentContractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subcontractor not found"));

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
            documentContract.setDocumentation(fileDocumentId);
        }

        documentContract.setStatus(documentContractRequestDto.getStatus() != null ? documentContractRequestDto.getStatus() : documentContract.getStatus());

        DocumentContract savedDocumentSubcontractor = documentContractRepository.save(documentContract);

        DocumentResponseDto documentSubcontractorResponse = DocumentResponseDto.builder()
                .idDocumentation(savedDocumentSubcontractor.getDocumentation())
                .title(savedDocumentSubcontractor.getTitle())
                .status(savedDocumentSubcontractor.getStatus())
                .documentation(savedDocumentSubcontractor.getDocumentation())
                .creationDate(savedDocumentSubcontractor.getCreationDate())
                .contract(savedDocumentSubcontractor.getContract().getIdContract())
                .build();

        return Optional.of(documentSubcontractorResponse);
    }

    @Override
    public void delete(String id) {
        documentContractRepository.deleteById(id);
    }

    @Override
    public Optional<DocumentResponseDto> upload(String id, MultipartFile file) throws IOException {
        FileDocument fileDocument = null;
        String fileDocumentId = null;
        FileDocument savedFileDocument= null;

        DocumentContract documentContract = documentContractRepository.findById(id).orElseThrow(() -> new NotFoundException("Document contract not found"));

        if (file != null && !file.isEmpty()) {
            try {
                fileDocument = FileDocument.builder()
                        .name(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .owner(documentContract.getContract()
                                instanceof ContractProviderSupplier
                                ? FileDocument.Owner.CONTRACT_SUPPLIER : FileDocument.Owner.SUBCONTRACTOR)
                        .ownerId(documentContract.getContract().getIdContract())
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
            documentContract.setDocumentation(fileDocumentId);
        }

        DocumentIAValidationResponse documentIAValidation = documentProcessingService.processDocument(file);

        if (documentIAValidation.isAutoValidate()) {
            if (documentIAValidation.isValid()) {
                documentContract.setStatus(Document.Status.APROVADO_IA);
            } else {
                documentContract.setStatus(Document.Status.REPROVADO_IA);
            }
        } else {
            documentContract.setStatus(Document.Status.EM_ANALISE);
        }

        documentContract.setVersionDate(LocalDateTime.now());

        DocumentContract savedDocumentContract = documentContractRepository.save(documentContract);

        DocumentResponseDto documentContractResponse = DocumentResponseDto.builder()
                .idDocumentation(savedDocumentContract.getIdDocumentation())
                .title(savedDocumentContract.getTitle())
                .status(savedDocumentContract.getStatus())
                .documentation(savedDocumentContract.getDocumentation())
                .creationDate(savedDocumentContract.getCreationDate())
                .contract(savedDocumentContract.getContract().getIdContract())
                .documentIAValidationResponse(documentIAValidation)
                .build();

        return Optional.of(documentContractResponse);
    }

    @Override
    public Page<DocumentResponseDto> findAllByContract(String idSearch, Pageable pageable) {
        Page<DocumentContract> documentSubcontractorPage = documentContractRepository.findAllByContract_IdContract(idSearch, pageable);

        Page<DocumentResponseDto> documentSubcontractorResponseDtoPage = documentSubcontractorPage.map(
                documentContract -> {
                    FileDocument fileDocument = null;
                    if (documentContract.getDocumentation() != null && ObjectId.isValid(documentContract.getDocumentation())) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentContract.getDocumentation()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }

                    return DocumentResponseDto.builder()
                            .idDocumentation(documentContract.getDocumentation())
                            .title(documentContract.getTitle())
                            .status(documentContract.getStatus())
                            .documentation(documentContract.getDocumentation())
                            .fileName(fileDocument != null ? fileDocument.getName() : null)
                            .fileContentType(fileDocument != null ? fileDocument.getContentType() : null)
                            .fileData(fileDocument != null ? fileDocument.getData() : null)
                            .creationDate(documentContract.getCreationDate())
                            .contract(documentContract.getContract().getIdContract())
                            .build();
                }
        );

        return documentSubcontractorResponseDtoPage;
    }

    @Override
    public DocumentResponseDto findAllSelectedDocuments(String id) {
        documentContractRepository.findById(id).orElseThrow(() -> new NotFoundException("Subcontractor not found"));
        List<DocumentContract> documentContract = documentContractRepository.findAllByContract_IdContract(id);
        List<DocumentMatrixResponseDto> selectedDocuments = documentContract.stream()
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
        DocumentResponseDto contractResponse = DocumentResponseDto.builder()
                .selectedDocumentsEnterprise(selectedDocuments)
                .nonSelectedDocumentsEnterprise(nonSelectedDocuments)
                .build();

        return contractResponse;
    }

    @Override
    public String updateRequiredDocuments(String id, List<String> documentCollection) {
        if (documentCollection == null || documentCollection.isEmpty()) {
            throw new NotFoundException("Invalid documents");
        }

        Contract contract = contractRepository.findById(id).orElseThrow(() -> new NotFoundException("Contract not found"));

        List<DocumentMatrix> documentMatrixList = documentMatrixRepository.findAllById(documentCollection);
        if (documentMatrixList.isEmpty()) {
            throw new NotFoundException("Documents not found");
        }

        List<DocumentContract> existingDocumentSubcontractors = documentContractRepository.findAllByContract_IdContract(id);

        Set<DocumentMatrix> existingDocuments = existingDocumentSubcontractors.stream()
                .map(DocumentContract::getDocumentMatrix)
                .collect(Collectors.toSet());

        List<DocumentContract> newDocumentSubcontractors = documentMatrixList.stream()
                .filter(doc -> !existingDocuments.contains(doc))
                .map(doc -> DocumentContract.builder()
                        .title(doc.getName())
                        .status(Document.Status.PENDENTE)
                        .contract(contract)
                        .documentMatrix(doc)
                        .build())
                .collect(Collectors.toList());

        List<DocumentContract> documentsToRemove = existingDocumentSubcontractors.stream()
                .filter(db -> !documentMatrixList.contains(db.getDocumentMatrix()))
                .collect(Collectors.toList());

        if (!documentsToRemove.isEmpty()) {
            documentContractRepository.deleteAll(documentsToRemove);
        }

        if (!newDocumentSubcontractors.isEmpty()) {
            documentContractRepository.saveAll(newDocumentSubcontractors);
        }

        return "Documents updated successfully";
    }

    @Override
    public String addRequiredDocument(String idEnterprise, String documentMatrixId) {
        if (documentMatrixId == null || documentMatrixId.isEmpty()) {
            throw new BadRequestException("Invalid documents");
        }

        Contract contract = contractRepository.findById(idEnterprise).orElseThrow(() -> new NotFoundException("Contract not found"));

        DocumentMatrix documentMatrix = documentMatrixRepository.findById(documentMatrixId).orElseThrow(() -> new NotFoundException("Document not found in matrix"));

        List<DocumentContract> existingDocumentBranches = documentContractRepository.findAllByContract_IdContract(idEnterprise);

        Set<DocumentMatrix> existingDocuments = existingDocumentBranches.stream()
                .map(DocumentContract::getDocumentMatrix)
                .collect(Collectors.toSet());

        DocumentContract newDocumentBranch = DocumentContract.builder()
                .title(documentMatrix.getName())
                .status(Document.Status.PENDENTE)
                .contract(contract)
                .documentMatrix(documentMatrix)
                .build();

        documentContractRepository.save(newDocumentBranch);

        return "Document updated successfully";
    }

    @Override
    public void removeRequiredDocument(String documentId) {
        if (documentId == null || documentId.isEmpty()) {
            throw new NotFoundException("Invalid documents");
        }
        documentContractRepository.deleteById(documentId);
    }
}


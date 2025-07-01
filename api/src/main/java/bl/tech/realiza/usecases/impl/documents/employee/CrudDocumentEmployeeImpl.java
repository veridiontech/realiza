package bl.tech.realiza.usecases.impl.documents.employee;

import bl.tech.realiza.domains.auditLogs.document.AuditLogDocument;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentClient;
import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.enums.AuditLogActions;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.documents.employee.DocumentEmployeeRepository;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.documents.employee.DocumentEmployeeRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentMatrixResponseDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;

import bl.tech.realiza.gateways.responses.services.DocumentIAValidationResponse;
import bl.tech.realiza.services.auth.JwtService;
import bl.tech.realiza.services.documentProcessing.DocumentProcessingService;
import bl.tech.realiza.usecases.impl.auditLogs.AuditLogServiceImpl;
import bl.tech.realiza.usecases.interfaces.auditLogs.AuditLogService;
import bl.tech.realiza.usecases.interfaces.documents.employee.CrudDocumentEmployee;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static bl.tech.realiza.domains.documents.Document.Status.*;

@Service
@RequiredArgsConstructor
public class CrudDocumentEmployeeImpl implements CrudDocumentEmployee {

    private final DocumentEmployeeRepository documentEmployeeRepository;
    private final EmployeeRepository employeeRepository;
    private final FileRepository fileRepository;
    private final DocumentMatrixRepository documentMatrixRepository;
    private final DocumentProcessingService documentProcessingService;
    private final UserRepository userRepository;
    private final AuditLogService auditLogServiceImpl;

    @Override
    public DocumentResponseDto save(DocumentEmployeeRequestDto documentEmployeeRequestDto, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Invalid file");
        }
        if (documentEmployeeRequestDto.getEmployee() == null || documentEmployeeRequestDto.getEmployee().isEmpty()) {
            throw new BadRequestException("Invalid employee");
        }

        FileDocument fileDocument = null;
        String fileDocumentId = null;

        Optional<Employee> employeeOptional = employeeRepository.findById(documentEmployeeRequestDto.getEmployee());

        Employee employee = employeeOptional.orElseThrow(() -> new EntityNotFoundException("Employee not found"));

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

        DocumentEmployee newDocumentEmployee = DocumentEmployee.builder()
                .title(documentEmployeeRequestDto.getTitle())
                .status(documentEmployeeRequestDto.getStatus())
                .documentation(fileDocumentId)
                .employee(employee)
                .build();

        DocumentEmployee savedDocumentEmployee = documentEmployeeRepository.save(newDocumentEmployee);

        DocumentResponseDto documentEmployeeResponseDto = DocumentResponseDto.builder()
                .idDocument(savedDocumentEmployee.getIdDocumentation())
                .title(savedDocumentEmployee.getTitle())
                .status(savedDocumentEmployee.getStatus())
                .documentation(savedDocumentEmployee.getDocumentation())
                .creationDate(savedDocumentEmployee.getCreationDate())
                .employee(savedDocumentEmployee.getEmployee() != null
                        ? savedDocumentEmployee.getEmployee().getIdEmployee()
                        : null)
                .build();

        return documentEmployeeResponseDto;
    }

    @Override
    public Optional<DocumentResponseDto> findOne(String id) {
        Optional<DocumentEmployee> documentEmployeeOptional = documentEmployeeRepository.findById(id);
        DocumentEmployee documentEmployee = documentEmployeeOptional.orElseThrow(() -> new EntityNotFoundException("DocumentEmployee not found"));

        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentEmployee.getDocumentation()));
        FileDocument fileDocument = fileDocumentOptional.orElseThrow(() -> new EntityNotFoundException("FileDocument not found"));

        DocumentResponseDto documentEmployeeResponseDto = DocumentResponseDto.builder()
                .idDocument(documentEmployee.getIdDocumentation())
                .title(documentEmployee.getTitle())
                .status(documentEmployee.getStatus())
                .documentation(documentEmployee.getDocumentation())
                .fileName(fileDocument.getName())
                .fileContentType(fileDocument.getContentType())
                .fileData(fileDocument.getData())
                .creationDate(documentEmployee.getCreationDate())
                .employee(documentEmployee.getEmployee() != null
                        ? documentEmployee.getEmployee().getIdEmployee()
                        : null)
                .build();

        return Optional.of(documentEmployeeResponseDto);
    }

    @Override
    public Page<DocumentResponseDto> findAll(Pageable pageable) {
        Page<DocumentEmployee> documentEmployeePage = documentEmployeeRepository.findAll(pageable);

        Page<DocumentResponseDto> documentEmployeeResponseDtoPage = documentEmployeePage.map(
                documentEmployee -> {
                    FileDocument fileDocument = null;
                    if (documentEmployee.getDocumentation() != null && ObjectId.isValid(documentEmployee.getDocumentation())) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentEmployee.getDocumentation()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }

                    return DocumentResponseDto.builder()
                            .idDocument(documentEmployee.getIdDocumentation())
                            .title(documentEmployee.getTitle())
                            .status(documentEmployee.getStatus())
                            .documentation(documentEmployee.getDocumentation())
                            .fileName(fileDocument != null ? fileDocument.getName() : null)
                            .fileContentType(fileDocument != null ? fileDocument.getContentType() : null)
                            .fileData(fileDocument != null ? fileDocument.getData() : null)
                            .creationDate(documentEmployee.getCreationDate())
                            .employee(documentEmployee.getEmployee() != null
                                    ? documentEmployee.getEmployee().getIdEmployee()
                                    : null)
                            .build();
                }
        );

        return documentEmployeeResponseDtoPage;
    }

    @Override
    public Optional<DocumentResponseDto> update(String id, DocumentEmployeeRequestDto documentEmployeeRequestDto, MultipartFile file) throws IOException {
        FileDocument fileDocument = null;
        String fileDocumentId = null;
        FileDocument savedFileDocument= null;

        Optional<DocumentEmployee> documentEmployeeOptional = documentEmployeeRepository.findById(id);

        DocumentEmployee documentEmployee = documentEmployeeOptional.orElseThrow(() -> new EntityNotFoundException("DocumentEmployee not found"));

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
            documentEmployee.setDocumentation(fileDocumentId);
        }

        documentEmployee.setStatus(documentEmployeeRequestDto.getStatus() != null ? documentEmployeeRequestDto.getStatus() : documentEmployee.getStatus());

        DocumentEmployee savedDocumentEmployee = documentEmployeeRepository.save(documentEmployee);

        DocumentResponseDto documentEmployeeResponseDto = DocumentResponseDto.builder()
                .idDocument(savedDocumentEmployee.getIdDocumentation())
                .title(savedDocumentEmployee.getTitle())
                .status(savedDocumentEmployee.getStatus())
                .documentation(savedDocumentEmployee.getDocumentation())
                .creationDate(savedDocumentEmployee.getCreationDate())
                .employee(savedDocumentEmployee.getEmployee() != null
                        ? savedDocumentEmployee.getEmployee().getIdEmployee()
                        : null)
                .build();

        return Optional.of(documentEmployeeResponseDto);
    }

    @Override
    public void delete(String id) {
        documentEmployeeRepository.deleteById(id);
    }

    @Override
    public Optional<DocumentResponseDto> upload(String id, MultipartFile file) throws IOException {
        if (file.getSize() > 5 * 1024 * 1024) { // 5 MB
            throw new BadRequestException("Arquivo muito grande.");
        }
        FileDocument fileDocument = null;
        String fileDocumentId = null;
        FileDocument savedFileDocument = null;

        DocumentEmployee documentEmployee = documentEmployeeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Document employee not found"));

        if (file != null && !file.isEmpty()) {
            try {
                fileDocument = FileDocument.builder()
                        .name(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .owner(FileDocument.Owner.EMPLOYEE)
                        .ownerId(documentEmployee.getEmployee().getIdEmployee())
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
            documentEmployee.setDocumentation(fileDocumentId);
            documentEmployee.setStatus(EM_ANALISE);
        }

        documentProcessingService.processDocumentAsync(file,
                (DocumentEmployee) Hibernate.unproxy(documentEmployee));

        DocumentEmployee savedDocumentEmployee = documentEmployeeRepository.save(documentEmployee);

        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            if (userResponsible != null) {
                auditLogServiceImpl.createAuditLogDocument(
                        savedDocumentEmployee,
                        userResponsible.getEmail() + " fez upload do documento "
                                + savedDocumentEmployee.getTitle() + " para o colaborador "
                                + (savedDocumentEmployee.getEmployee() != null
                                    ? savedDocumentEmployee.getEmployee().getName()
                                    : "Not identified"),
                        AuditLogActions.UPLOAD,
                        userResponsible);
            }
        }

        DocumentResponseDto documentEmployeeResponse = DocumentResponseDto.builder()
                .idDocument(savedDocumentEmployee.getIdDocumentation())
                .title(savedDocumentEmployee.getTitle())
                .status(savedDocumentEmployee.getStatus())
                .documentation(savedDocumentEmployee.getDocumentation())
                .creationDate(savedDocumentEmployee.getCreationDate())
                .employee(savedDocumentEmployee.getEmployee() != null
                        ? savedDocumentEmployee.getEmployee().getIdEmployee()
                        : null)
                .build();

        return Optional.of(documentEmployeeResponse);
    }

    @Override
    public Page<DocumentResponseDto> findAllByEmployee(String idSearch, Pageable pageable) {
        Page<DocumentEmployee> documentEmployeePage = documentEmployeeRepository.findAllByEmployee_IdEmployee(idSearch, pageable);

        Page<DocumentResponseDto> documentEmployeeResponseDtoPage = documentEmployeePage.map(
                documentEmployee -> {
                    FileDocument fileDocument = null;
                    if (documentEmployee.getDocumentation() != null && ObjectId.isValid(documentEmployee.getDocumentation())) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentEmployee.getDocumentation()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }

                    return DocumentResponseDto.builder()
                            .idDocument(documentEmployee.getIdDocumentation())
                            .title(documentEmployee.getTitle())
                            .status(documentEmployee.getStatus())
                            .documentation(documentEmployee.getDocumentation())
                            .fileName(fileDocument != null ? fileDocument.getName() : null)
                            .fileContentType(fileDocument != null ? fileDocument.getContentType() : null)
                            .fileData(fileDocument != null ? fileDocument.getData() : null)
                            .creationDate(documentEmployee.getCreationDate())
                            .employee(documentEmployee.getEmployee() != null
                                    ? documentEmployee.getEmployee().getIdEmployee()
                                    : null)
                            .build();
                }
        );

        return documentEmployeeResponseDtoPage;
    }

    @Override
    public DocumentResponseDto findAllSelectedDocuments(String id) {
        employeeRepository.findById(id).orElseThrow(() -> new NotFoundException("Employee not found"));
        List<DocumentEmployee> documentEmployee = documentEmployeeRepository.findAllByEmployee_IdEmployee(id);
        List<DocumentMatrixResponseDto> selectedDocuments = documentEmployee.stream()
                .sorted(Comparator.comparing(db -> db.getDocumentMatrix().getName()))
                .map(doc -> DocumentMatrixResponseDto.builder()
                        .documentId(doc.getIdDocumentation()) // ID do DocumentBranch
                        .idDocumentMatrix(doc.getDocumentMatrix().getIdDocument())
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
        List<DocumentMatrixResponseDto> allDocuments = documentMatrixRepository.findAllBySubGroup_Group_GroupName("Documento empresa")
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
        List<DocumentMatrixResponseDto> nonSelectedDocuments = new ArrayList<>(allDocuments);
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

        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new NotFoundException("Employee not found"));

        List<DocumentMatrix> documentMatrixList = documentMatrixRepository.findAllById(documentCollection);
        if (documentMatrixList.isEmpty()) {
            throw new NotFoundException("Documents not found");
        }

        List<DocumentEmployee> existingDocumentEmployee = documentEmployeeRepository.findAllByEmployee_IdEmployee(id);

        Set<DocumentMatrix> existingDocuments = existingDocumentEmployee.stream()
                .map(DocumentEmployee::getDocumentMatrix)
                .collect(Collectors.toSet());

        List<DocumentEmployee> newDocumentBranches = documentMatrixList.stream()
                .filter(doc -> !existingDocuments.contains(doc))
                .map(doc -> DocumentEmployee.builder()
                        .title(doc.getName())
                        .status(PENDENTE)
                        .employee(employee)
                        .documentMatrix(doc)
                        .build())
                .collect(Collectors.toList());

        List<DocumentEmployee> documentsToRemove = existingDocumentEmployee.stream()
                .filter(db -> !documentMatrixList.contains(db.getDocumentMatrix()))
                .collect(Collectors.toList());

        if (!documentsToRemove.isEmpty()) {
            documentEmployeeRepository.deleteAll(documentsToRemove);
        }

        if (!newDocumentBranches.isEmpty()) {
            documentEmployeeRepository.saveAll(newDocumentBranches);
        }

        return "Documents updated successfully";
    }

    @Override
    public String solicitateNewRequiredDocument(String id, String documentId) {
        if (documentId == null || documentId.isEmpty()) {
            throw new NotFoundException("Invalid documents");
        }

        DocumentMatrix documentMatrix = documentMatrixRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document not found"));

        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new NotFoundException("Employee not found"));

        DocumentEmployee documentEmployee = DocumentEmployee.builder()
                .employee(employee)
                .documentMatrix(documentMatrix)
                .request(Document.Request.ADD)
                .title(documentMatrix.getName())
                .build();

        documentEmployeeRepository.save(documentEmployee);

        return "Document requested successfully";
    }

    @Override
    public String addRequiredDocument(String idEnterprise, String documentMatrixId) {
        if (documentMatrixId == null || documentMatrixId.isEmpty()) {
            throw new BadRequestException("Invalid documents");
        }

        Employee employee = employeeRepository.findById(idEnterprise).orElseThrow(() -> new NotFoundException("Employee not found"));

        DocumentMatrix documentMatrix = documentMatrixRepository.findById(documentMatrixId).orElseThrow(() -> new NotFoundException("Document not found in matrix"));

        List<DocumentEmployee> existingDocumentBranches = documentEmployeeRepository.findAllByEmployee_IdEmployee(idEnterprise);

        Set<DocumentMatrix> existingDocuments = existingDocumentBranches.stream()
                .map(DocumentEmployee::getDocumentMatrix)
                .collect(Collectors.toSet());

        DocumentEmployee newDocumentBranch = DocumentEmployee.builder()
                .title(documentMatrix.getName())
                .status(PENDENTE)
                .employee(employee)
                .documentMatrix(documentMatrix)
                .build();

        documentEmployeeRepository.save(newDocumentBranch);

        return "Document updated successfully";
    }

    @Override
    public void removeRequiredDocument(String documentId) {
        if (documentId == null || documentId.isEmpty()) {
            throw new NotFoundException("Invalid documents");
        }
        documentEmployeeRepository.deleteById(documentId);
    }
}

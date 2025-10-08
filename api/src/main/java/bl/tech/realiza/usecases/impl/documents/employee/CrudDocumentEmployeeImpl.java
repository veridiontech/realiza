package bl.tech.realiza.usecases.impl.documents.employee;

import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.contracts.RequirementRepository;
import bl.tech.realiza.gateways.repositories.documents.employee.DocumentEmployeeRepository;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.documents.employee.DocumentEmployeeRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentMatrixResponseDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;

import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.services.GoogleCloudService;
import bl.tech.realiza.services.auth.JwtService;
import bl.tech.realiza.services.documentProcessing.DocumentProcessingService;
import bl.tech.realiza.usecases.interfaces.auditLogs.AuditLogService;
import bl.tech.realiza.usecases.interfaces.documents.employee.CrudDocumentEmployee;
import bl.tech.realiza.usecases.interfaces.users.CrudNotification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
public class CrudDocumentEmployeeImpl implements CrudDocumentEmployee {

    private final DocumentEmployeeRepository documentEmployeeRepository;
    private final EmployeeRepository employeeRepository;
    private final FileRepository fileRepository;
    private final DocumentMatrixRepository documentMatrixRepository;
    private final DocumentProcessingService documentProcessingService;
    private final UserRepository userRepository;
    private final AuditLogService auditLogServiceImpl;
    private final JwtService jwtService;
    private final GoogleCloudService googleCloudService;
    private final CrudNotification crudNotification;
    private final RequirementRepository requirementRepository;

    @Override
    public DocumentResponseDto save(DocumentEmployeeRequestDto documentEmployeeRequestDto) {
        if (documentEmployeeRequestDto.getEmployee() == null || documentEmployeeRequestDto.getEmployee().isEmpty()) {
            throw new BadRequestException("Invalid employee");
        }

        Employee employee = employeeRepository.findById(documentEmployeeRequestDto.getEmployee())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        DocumentMatrix matrix = documentMatrixRepository.findById(documentEmployeeRequestDto.getDocumentMatrixId())
                .orElseThrow(() -> new NotFoundException("Document Matrix not found"));

        DocumentEmployee savedDocumentEmployee = documentEmployeeRepository.save(DocumentEmployee.builder()
                .title(documentEmployeeRequestDto.getTitle())
                .status(documentEmployeeRequestDto.getStatus())
                .employee(employee)
                        .documentMatrix(matrix)
                .build());

        return DocumentResponseDto.builder()
                .idDocument(savedDocumentEmployee.getIdDocumentation())
                .title(savedDocumentEmployee.getTitle())
                .status(savedDocumentEmployee.getStatus())
                .creationDate(savedDocumentEmployee.getCreationDate())
                .employee(savedDocumentEmployee.getEmployee() != null
                        ? savedDocumentEmployee.getEmployee().getIdEmployee()
                        : null)
                .build();
    }

    @Override
    public Optional<DocumentResponseDto> findOne(String id) {
        DocumentEmployee documentEmployee = documentEmployeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DocumentEmployee not found"));

        String signedUrl = null;
        FileDocument fileDocument = documentEmployee.getDocument().stream()
                .max(Comparator.comparing(FileDocument::getCreationDate))
                .orElse(null);
        if (fileDocument != null) {
            if (fileDocument.getUrl() != null) {
                signedUrl = googleCloudService.generateSignedUrl(fileDocument.getUrl(), 15);
            }
        }

        DocumentResponseDto documentEmployeeResponseDto = DocumentResponseDto.builder()
                .idDocument(documentEmployee.getIdDocumentation())
                .title(documentEmployee.getTitle())
                .status(documentEmployee.getStatus())
                .signedUrl(signedUrl)
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

        return documentEmployeePage.map(
                documentEmployee -> {
                    String signedUrl = null;
                    FileDocument fileDocument = documentEmployee.getDocument().stream()
                            .max(Comparator.comparing(FileDocument::getCreationDate))
                            .orElse(null);
                    if (fileDocument != null) {
                        if (fileDocument.getUrl() != null) {
                            signedUrl = googleCloudService.generateSignedUrl(fileDocument.getUrl(), 15);
                        }
                    }

                    return DocumentResponseDto.builder()
                            .idDocument(documentEmployee.getIdDocumentation())
                            .title(documentEmployee.getTitle())
                            .status(documentEmployee.getStatus())
                            .signedUrl(signedUrl)
                            .creationDate(documentEmployee.getCreationDate())
                            .employee(documentEmployee.getEmployee() != null
                                    ? documentEmployee.getEmployee().getIdEmployee()
                                    : null)
                            .build();
                }
        );
    }

    @Override
    public Optional<DocumentResponseDto> update(String id, DocumentEmployeeRequestDto documentEmployeeRequestDto) {
        DocumentEmployee documentEmployee = documentEmployeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DocumentEmployee not found"));

        documentEmployee.setStatus(documentEmployeeRequestDto.getStatus() != null
                ? documentEmployeeRequestDto.getStatus()
                : documentEmployee.getStatus());

        DocumentEmployee savedDocumentEmployee = documentEmployeeRepository.save(documentEmployee);

        return Optional.of(DocumentResponseDto.builder()
                .idDocument(savedDocumentEmployee.getIdDocumentation())
                .title(savedDocumentEmployee.getTitle())
                .status(savedDocumentEmployee.getStatus())
                .creationDate(savedDocumentEmployee.getCreationDate())
                .employee(savedDocumentEmployee.getEmployee() != null
                        ? savedDocumentEmployee.getEmployee().getIdEmployee()
                        : null)
                .build());
    }

    @Override
    public void delete(String id) {
        documentEmployeeRepository.deleteById(id);
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

        DocumentEmployee documentEmployee = documentEmployeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document employee not found"));

        if (file != null && !file.isEmpty()) {
            try {
                String gcsUrl = googleCloudService.uploadFile(file, "documents/employee");

                savedFileDocument = fileRepository.save(FileDocument.builder()
                        .name(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .url(gcsUrl)
                        .document(documentEmployee)
                        .canBeOverwritten(documentEmployee.getDocumentMatrix().getIsDocumentUnique())
                        .build());
                signedUrl = googleCloudService.generateSignedUrl(savedFileDocument.getUrl(), 15);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new EntityNotFoundException(e);
            }
            documentEmployee.setStatus(EM_ANALISE);
            documentEmployee.setAdherent(true);
            documentEmployee.setConforming(false);
        }

        DocumentEmployee savedDocumentEmployee = documentEmployeeRepository.save(documentEmployee);

        documentProcessingService.processDocumentAsync(file,
                (DocumentEmployee) Hibernate.unproxy(documentEmployee));

        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            if (userResponsible != null) {
                auditLogServiceImpl.createAuditLog(
                        savedDocumentEmployee.getIdDocumentation(),
                        DOCUMENT,
                        userResponsible.getFullName() + " fez upload do documento "
                                + savedDocumentEmployee.getTitle() + " para o colaborador "
                                + savedDocumentEmployee.getEmployee().getFullName(),
                        null,
                        null,
                        UPLOAD,
                        userResponsible.getIdUser());
            }
        }

        crudNotification.saveDocumentNotificationForRealizaUsers(savedDocumentEmployee.getIdDocumentation());

        DocumentResponseDto documentEmployeeResponse = DocumentResponseDto.builder()
                .idDocument(savedDocumentEmployee.getIdDocumentation())
                .title(savedDocumentEmployee.getTitle())
                .status(savedDocumentEmployee.getStatus())
                .signedUrl(signedUrl)
                .creationDate(savedDocumentEmployee.getCreationDate())
                .employee(savedDocumentEmployee.getEmployee() != null
                        ? savedDocumentEmployee.getEmployee().getIdEmployee()
                        : null)
                .build();

        return Optional.of(documentEmployeeResponse);
    }

    @Override
    public Page<DocumentResponseDto> findAllByEmployee(String idSearch, Pageable pageable) {
        UserResponseDto requester = jwtService.extractAllClaims(jwtService.getTokenFromRequest());
        List<DocumentEmployee> allDocuments = new ArrayList<>();

        if ((requester.getAdmin() != null ? requester.getAdmin() : false)
                || requester.getRole().equals(User.Role.ROLE_REALIZA_BASIC)
                || requester.getRole().equals(User.Role.ROLE_REALIZA_PLUS)) {
        allDocuments.addAll(documentEmployeeRepository.findAllByEmployee_IdEmployee(idSearch, pageable).getContent());
        } else {
            if (requester.getLaboral()) {
                allDocuments.addAll(documentEmployeeRepository.findAllByEmployee_IdEmployeeAndType(idSearch, "trabalhista",pageable).getContent());
            }
            if (requester.getWorkplaceSafety()) {
                allDocuments.addAll(documentEmployeeRepository.findAllByEmployee_IdEmployeeAndType(idSearch, "segurança do trabalho",pageable).getContent());
            }
            if (requester.getRegistrationAndCertificates()) {
                allDocuments.addAll(documentEmployeeRepository.findAllByEmployee_IdEmployeeAndType(idSearch, "meio ambiente",pageable).getContent());
            }
            if (requester.getGeneral()) {
                allDocuments.addAll(documentEmployeeRepository.findAllByEmployee_IdEmployeeAndType(idSearch, "cadastro e certidões",pageable).getContent());
            }
            if (requester.getHealth()) {
                allDocuments.addAll(documentEmployeeRepository.findAllByEmployee_IdEmployeeAndType(idSearch, "geral",pageable).getContent());
            }
            if (requester.getEnvironment()) {
                allDocuments.addAll(documentEmployeeRepository.findAllByEmployee_IdEmployeeAndType(idSearch, "saude",pageable).getContent());
            }
        }


        return new PageImpl<>(allDocuments.stream()
                .sorted(Comparator.comparing(DocumentEmployee::getAssignmentDate).reversed())
                .limit(pageable.getPageSize())
                .map(
                documentEmployee -> {
                    String signedUrl = null;
                    FileDocument fileDocument = documentEmployee.getDocument().stream()
                            .max(Comparator.comparing(FileDocument::getCreationDate))
                            .orElse(null);
                    if (fileDocument != null) {
                        if (fileDocument.getUrl() != null) {
                            signedUrl = googleCloudService.generateSignedUrl(fileDocument.getUrl(), 15);
                        }
                    }

                    return DocumentResponseDto.builder()
                            .idDocument(documentEmployee.getIdDocumentation())
                            .title(documentEmployee.getTitle())
                            .status(documentEmployee.getStatus())
                            .creationDate(documentEmployee.getCreationDate())
                            .signedUrl(signedUrl)
                            .employee(documentEmployee.getEmployee() != null
                                    ? documentEmployee.getEmployee().getIdEmployee()
                                    : null)
                            .build();
                }).collect(Collectors.toList()), pageable, allDocuments.size());
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
        List<DocumentMatrixResponseDto> allDocuments = documentMatrixRepository.findAllByGroup_GroupName("Documento empresa")
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

        return DocumentResponseDto.builder()
                .selectedDocumentsEnterprise(selectedDocuments)
                .nonSelectedDocumentsEnterprise(nonSelectedDocuments)
                .build();
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
    public String solicitNewRequiredDocument(String id, String documentId) {
        if (documentId == null || documentId.isEmpty()) {
            throw new NotFoundException("Invalid documents");
        }

        DocumentMatrix documentMatrix = documentMatrixRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document not found"));

        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new NotFoundException("Employee not found"));

        DocumentEmployee documentEmployee = DocumentEmployee.builder()
                .employee(employee)
                .documentMatrix(documentMatrix)
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

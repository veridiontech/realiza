package bl.tech.realiza.usecases.impl.documents.employee;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.documents.employee.DocumentEmployeeRepository;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.requests.documents.employee.DocumentEmployeeRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;

import bl.tech.realiza.usecases.interfaces.documents.employee.CrudDocumentEmployee;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CrudDocumentEmployeeImpl implements CrudDocumentEmployee {

    private final DocumentEmployeeRepository documentEmployeeRepository;
    private final EmployeeRepository employeeRepository;
    private final FileRepository fileRepository;
    private final DocumentMatrixRepository documentMatrixRepository;

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
                .idDocumentation(savedDocumentEmployee.getIdDocumentation())
                .title(savedDocumentEmployee.getTitle())
                .status(savedDocumentEmployee.getStatus())
                .documentation(savedDocumentEmployee.getDocumentation())
                .creationDate(savedDocumentEmployee.getCreationDate())
                .employee(savedDocumentEmployee.getEmployee().getIdEmployee())
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
                .idDocumentation(documentEmployee.getIdDocumentation())
                .title(documentEmployee.getTitle())
                .status(documentEmployee.getStatus())
                .documentation(documentEmployee.getDocumentation())
                .fileName(fileDocument.getName())
                .fileContentType(fileDocument.getContentType())
                .fileData(fileDocument.getData())
                .creationDate(documentEmployee.getCreationDate())
                .employee(documentEmployee.getEmployee().getIdEmployee())
                .build();

        return Optional.of(documentEmployeeResponseDto);
    }

    @Override
    public Page<DocumentResponseDto> findAll(Pageable pageable) {
        Page<DocumentEmployee> documentEmployeePage = documentEmployeeRepository.findAll(pageable);

        Page<DocumentResponseDto> documentEmployeeResponseDtoPage = documentEmployeePage.map(
                documentEmployee -> {
                    Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentEmployee.getDocumentation()));
                    FileDocument fileDocument = fileDocumentOptional.orElse(null);

                    return DocumentResponseDto.builder()
                            .idDocumentation(documentEmployee.getIdDocumentation())
                            .title(documentEmployee.getTitle())
                            .status(documentEmployee.getStatus())
                            .documentation(documentEmployee.getDocumentation())
                            .fileName(fileDocument.getName())
                            .fileContentType(fileDocument.getContentType())
                            .fileData(fileDocument.getData())
                            .creationDate(documentEmployee.getCreationDate())
                            .employee(documentEmployee.getEmployee().getIdEmployee())
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
                .idDocumentation(savedDocumentEmployee.getIdDocumentation())
                .title(savedDocumentEmployee.getTitle())
                .status(savedDocumentEmployee.getStatus())
                .documentation(savedDocumentEmployee.getDocumentation())
                .creationDate(savedDocumentEmployee.getCreationDate())
                .employee(savedDocumentEmployee.getEmployee().getIdEmployee())
                .build();

        return Optional.of(documentEmployeeResponseDto);
    }

    @Override
    public void delete(String id) {
        documentEmployeeRepository.deleteById(id);
    }

    @Override
    public Page<DocumentResponseDto> findAllByEmployee(String idSearch, Pageable pageable) {
        Page<DocumentEmployee> documentEmployeePage = documentEmployeeRepository.findAllByEmployee_IdEmployee(idSearch, pageable);

        Page<DocumentResponseDto> documentEmployeeResponseDtoPage = documentEmployeePage.map(
                documentEmployee -> {
                    Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentEmployee.getDocumentation()));
                    FileDocument fileDocument = fileDocumentOptional.orElse(null);

                    return DocumentResponseDto.builder()
                            .idDocumentation(documentEmployee.getIdDocumentation())
                            .title(documentEmployee.getTitle())
                            .status(documentEmployee.getStatus())
                            .documentation(documentEmployee.getDocumentation())
                            .fileName(fileDocument.getName())
                            .fileContentType(fileDocument.getContentType())
                            .fileData(fileDocument.getData())
                            .creationDate(documentEmployee.getCreationDate())
                            .employee(documentEmployee.getEmployee().getIdEmployee())
                            .build();
                }
        );

        return documentEmployeeResponseDtoPage;
    }

    @Override
    public DocumentResponseDto findAllSelectedDocuments(String id) {
        employeeRepository.findById(id).orElseThrow(() -> new NotFoundException("Employee not found"));
        List<DocumentEmployee> documentEmployee = documentEmployeeRepository.findAllByEmployee_IdEmployee(id);
        List<DocumentMatrix> selectedDocuments = documentEmployee.stream().map(DocumentEmployee::getDocumentMatrix).collect(Collectors.toList());
        List<DocumentMatrix> allDocuments = documentMatrixRepository.findAllBySubGroup_Group_GroupName("Documento empresa");
        List<DocumentMatrix> nonSelectedDocuments = new ArrayList<>(allDocuments);
        nonSelectedDocuments.removeAll(selectedDocuments);
        DocumentResponseDto employeeResponse = DocumentResponseDto.builder()
                .selectedDocumentsEnterprise(selectedDocuments)
                .nonSelectedDocumentsEnterprise(nonSelectedDocuments)
                .build();

        return employeeResponse;
    }

    @Override
    public String updateDocumentRequests(String id, List<String> documentCollection) {
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
                        .status(Document.Status.PENDENTE)
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
}

package bl.tech.realiza.usecases.impl.documents.employee;

import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.gateways.repositories.documents.employee.DocumentEmployeeRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.requests.documents.employee.DocumentEmployeeRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;

import bl.tech.realiza.services.documentProcessing.DocumentProcessingService;
import bl.tech.realiza.usecases.interfaces.documents.employee.CrudDocumentEmployee;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.TesseractException;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudDocumentEmployeeImpl implements CrudDocumentEmployee {

    private final DocumentEmployeeRepository documentEmployeeRepository;
    private final EmployeeRepository employeeRepository;
    private final FileRepository fileRepository;

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
}

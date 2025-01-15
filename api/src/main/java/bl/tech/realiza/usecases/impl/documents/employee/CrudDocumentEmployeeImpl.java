package bl.tech.realiza.usecases.impl.documents.employee;

import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.gateways.repositories.documents.employee.DocumentEmployeeRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.requests.documents.employee.DocumentEmployeeRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.employee.CrudDocumentEmployee;
import lombok.RequiredArgsConstructor;
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
        Optional<Employee> employeeOptional = employeeRepository.findById(documentEmployeeRequestDto.getEmployee());

        Employee employee = employeeOptional.orElseThrow(() -> new RuntimeException("Employee not found"));

        FileDocument fileDocument = FileDocument.builder()
                .name(file.getOriginalFilename())
                .contentType(file.getContentType())
                .data(file.getBytes())
                .build();

        FileDocument savedFileDocument= fileRepository.save(fileDocument);

        DocumentEmployee newDocumentEmployee = DocumentEmployee.builder()
                .title(documentEmployeeRequestDto.getTitle())
                .risk(documentEmployeeRequestDto.getRisk())
                .status(documentEmployeeRequestDto.getStatus())
                .documentation(savedFileDocument.getIdDocument())
                .creationDate(documentEmployeeRequestDto.getCreationDate())
                .employee(employee)
                .build();

        DocumentEmployee savedDocumentEmployee = documentEmployeeRepository.save(newDocumentEmployee);

        DocumentResponseDto documentEmployeeResponseDto = DocumentResponseDto.builder()
                .idDocumentation(savedDocumentEmployee.getIdDocumentation())
                .title(savedDocumentEmployee.getTitle())
                .risk(savedDocumentEmployee.getRisk())
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

        DocumentEmployee documentEmployee = documentEmployeeOptional.orElseThrow(() -> new RuntimeException("DocumentEmployee not found"));

        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(documentEmployee.getDocumentation());
        FileDocument fileDocument = fileDocumentOptional.orElseThrow(() -> new RuntimeException("FileDocument not found"));

        DocumentResponseDto documentEmployeeResponseDto = DocumentResponseDto.builder()
                .idDocumentation(documentEmployee.getIdDocumentation())
                .title(documentEmployee.getTitle())
                .risk(documentEmployee.getRisk())
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
                    Optional<FileDocument> fileDocumentOptional = fileRepository.findById(documentEmployee.getDocumentation());
                    FileDocument fileDocument = fileDocumentOptional.orElse(null);

                    return DocumentResponseDto.builder()
                            .idDocumentation(documentEmployee.getIdDocumentation())
                            .title(documentEmployee.getTitle())
                            .risk(documentEmployee.getRisk())
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
    public Optional<DocumentResponseDto> update(DocumentEmployeeRequestDto documentEmployeeRequestDto, MultipartFile file) throws IOException {
        Optional<DocumentEmployee> documentEmployeeOptional = documentEmployeeRepository.findById(documentEmployeeRequestDto.getIdDocumentation());

        DocumentEmployee documentEmployee = documentEmployeeOptional.orElseThrow(() -> new RuntimeException("DocumentEmployee not found"));

        if (file != null && !file.isEmpty()) {
            // Process the file if it exists
            FileDocument fileDocument = FileDocument.builder()
                    .name(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .data(file.getBytes()) // Handle the IOException
                    .build();

            FileDocument savedFileDocument = fileRepository.save(fileDocument);

            // Update the documentBranch with the new file's ID
            documentEmployee.setDocumentation(savedFileDocument.getIdDocument());
        }

        documentEmployee.setTitle(documentEmployeeRequestDto.getTitle() != null ? documentEmployeeRequestDto.getTitle() : documentEmployee.getTitle());
        documentEmployee.setRisk(documentEmployeeRequestDto.getRisk() != null ? documentEmployeeRequestDto.getRisk() : documentEmployee.getRisk());
        documentEmployee.setStatus(documentEmployeeRequestDto.getStatus() != null ? documentEmployeeRequestDto.getStatus() : documentEmployee.getStatus());
        documentEmployee.setCreationDate(documentEmployeeRequestDto.getCreationDate() != null ? documentEmployeeRequestDto.getCreationDate() : documentEmployee.getCreationDate());
        documentEmployee.setIsActive(documentEmployeeRequestDto.getIsActive() != null ? documentEmployeeRequestDto.getIsActive() : documentEmployee.getIsActive());

        DocumentEmployee savedDocumentEmployee = documentEmployeeRepository.save(documentEmployee);

        DocumentResponseDto documentEmployeeResponseDto = DocumentResponseDto.builder()
                .idDocumentation(savedDocumentEmployee.getIdDocumentation())
                .title(savedDocumentEmployee.getTitle())
                .risk(savedDocumentEmployee.getRisk())
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
}

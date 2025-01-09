package bl.tech.realiza.usecases.impl.documents.employee;

import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.gateways.repositories.documents.employee.DocumentEmployeeRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.requests.documents.employee.DocumentEmployeeRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.employee.CrudDocumentEmployee;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudDocumentEmployeeImpl implements CrudDocumentEmployee {

    private final DocumentEmployeeRepository documentEmployeeRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public DocumentResponseDto save(DocumentEmployeeRequestDto documentEmployeeRequestDto) {
        Optional<Employee> employeeOptional = employeeRepository.findById(documentEmployeeRequestDto.getEmployee());

        Employee employee = employeeOptional.orElseThrow(() -> new RuntimeException("Employee not found"));

        DocumentEmployee newDocumentEmployee = DocumentEmployee.builder()
                .title(documentEmployeeRequestDto.getTitle())
                .risk(documentEmployeeRequestDto.getRisk())
                .status(documentEmployeeRequestDto.getStatus())
                .documentation(documentEmployeeRequestDto.getDocumentation())
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

        DocumentResponseDto documentEmployeeResponseDto = DocumentResponseDto.builder()
                .idDocumentation(documentEmployee.getIdDocumentation())
                .title(documentEmployee.getTitle())
                .risk(documentEmployee.getRisk())
                .status(documentEmployee.getStatus())
                .documentation(documentEmployee.getDocumentation())
                .creationDate(documentEmployee.getCreationDate())
                .employee(documentEmployee.getEmployee().getIdEmployee())
                .build();

        return Optional.of(documentEmployeeResponseDto);
    }

    @Override
    public Page<DocumentResponseDto> findAll(Pageable pageable) {
        Page<DocumentEmployee> documentEmployeePage = documentEmployeeRepository.findAll(pageable);

        Page<DocumentResponseDto> documentEmployeeResponseDtoPage = documentEmployeePage.map(
                documentEmployee -> DocumentResponseDto.builder()
                        .idDocumentation(documentEmployee.getIdDocumentation())
                        .title(documentEmployee.getTitle())
                        .risk(documentEmployee.getRisk())
                        .status(documentEmployee.getStatus())
                        .documentation(documentEmployee.getDocumentation())
                        .creationDate(documentEmployee.getCreationDate())
                        .employee(documentEmployee.getEmployee().getIdEmployee())
                        .build()
        );

        return documentEmployeeResponseDtoPage;
    }

    @Override
    public Optional<DocumentResponseDto> update(DocumentEmployeeRequestDto documentEmployeeRequestDto) {
        Optional<DocumentEmployee> documentEmployeeOptional = documentEmployeeRepository.findById(documentEmployeeRequestDto.getIdDocumentation());

        DocumentEmployee documentEmployee = documentEmployeeOptional.orElseThrow(() -> new RuntimeException("DocumentEmployee not found"));

        documentEmployee.setTitle(documentEmployeeRequestDto.getTitle() != null ? documentEmployeeRequestDto.getTitle() : documentEmployee.getTitle());
        documentEmployee.setRisk(documentEmployeeRequestDto.getRisk() != null ? documentEmployeeRequestDto.getRisk() : documentEmployee.getRisk());
        documentEmployee.setStatus(documentEmployeeRequestDto.getStatus() != null ? documentEmployeeRequestDto.getStatus() : documentEmployee.getStatus());
        documentEmployee.setDocumentation(documentEmployeeRequestDto.getDocumentation() != null ? documentEmployeeRequestDto.getDocumentation() : documentEmployee.getDocumentation());
        documentEmployee.setCreationDate(documentEmployeeRequestDto.getCreationDate() != null ? documentEmployeeRequestDto.getCreationDate() : documentEmployee.getCreationDate());

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

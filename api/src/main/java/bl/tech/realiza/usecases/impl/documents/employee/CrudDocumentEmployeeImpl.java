package bl.tech.realiza.usecases.impl.documents.employee;

import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.gateways.repositories.documents.employee.DocumentEmployeeRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeBrazilianRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.requests.documents.employee.DocumentEmployeeRequestDto;
import bl.tech.realiza.gateways.responses.documents.employee.DocumentEmployeeResponseDto;
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
    public DocumentEmployeeResponseDto save(DocumentEmployeeRequestDto documentEmployeeRequestDto) {

        Optional<Employee> employeeOptional = employeeRepository.findById(documentEmployeeRequestDto.getEmployee());

        Employee employee = employeeOptional.orElseThrow(() -> new RuntimeException("Employee not found"));

        DocumentEmployee newDocumentEmployee = DocumentEmployee.builder()
                .title(documentEmployeeRequestDto.getTitle())
                .risk(documentEmployeeRequestDto.getRisk())
                .status(documentEmployeeRequestDto.getStatus())
                .documentation(documentEmployeeRequestDto.getDocumentation())
                .creation_date(documentEmployeeRequestDto.getCreation_date())
                .employee(employee)
                .build();

        DocumentEmployee savedDocumentEmployee = documentEmployeeRepository.save(newDocumentEmployee);

        DocumentEmployeeResponseDto documentEmployeeResponseDto = DocumentEmployeeResponseDto.builder()
                .id_documentation(savedDocumentEmployee.getId_documentation())
                .title(savedDocumentEmployee.getTitle())
                .risk(savedDocumentEmployee.getRisk())
                .status(savedDocumentEmployee.getStatus())
                .documentation(savedDocumentEmployee.getDocumentation())
                .creation_date(savedDocumentEmployee.getCreation_date())
                .employee(savedDocumentEmployee.getEmployee().getId_employee())
                .build();

        return documentEmployeeResponseDto;
    }

    @Override
    public Optional<DocumentEmployeeResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<DocumentEmployeeResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<DocumentEmployeeResponseDto> update(DocumentEmployeeRequestDto documentEmployeeRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}

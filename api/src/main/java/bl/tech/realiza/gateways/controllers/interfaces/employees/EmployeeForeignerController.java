package bl.tech.realiza.gateways.controllers.interfaces.employees;

import bl.tech.realiza.gateways.requests.employees.EmployeeForeignerRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeForeignerResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface EmployeeForeignerController {
    ResponseEntity<EmployeeForeignerResponseDto> createEmployeeForeigner(EmployeeForeignerRequestDto branchRequestDto);
    ResponseEntity<Optional<EmployeeForeignerResponseDto>> getOneEmployeeForeigner(String id);
    ResponseEntity<Page<EmployeeForeignerResponseDto>> getAllEmployeesForeigner(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<EmployeeForeignerResponseDto>> updateEmployeeForeigner(EmployeeForeignerRequestDto branchRequestDto);
    ResponseEntity<Void> deleteEmployeeForeigner(String id);
}

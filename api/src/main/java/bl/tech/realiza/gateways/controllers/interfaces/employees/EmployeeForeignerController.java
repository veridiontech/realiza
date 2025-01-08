package bl.tech.realiza.gateways.controllers.interfaces.employees;

import bl.tech.realiza.gateways.requests.employees.EmployeeForeignerRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface EmployeeForeignerController {
    ResponseEntity<EmployeeResponseDto> createEmployeeForeigner(EmployeeForeignerRequestDto employeeForeignerRequestDto);
    ResponseEntity<Optional<EmployeeResponseDto>> getOneEmployeeForeigner(String id);
    ResponseEntity<Page<EmployeeResponseDto>> getAllEmployeesForeigner(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<EmployeeResponseDto>> updateEmployeeForeigner(EmployeeForeignerRequestDto employeeForeignerRequestDto);
    ResponseEntity<Void> deleteEmployeeForeigner(String id);
}

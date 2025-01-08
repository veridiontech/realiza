package bl.tech.realiza.gateways.controllers.interfaces.employees;

import bl.tech.realiza.gateways.requests.employees.EmployeeBrazilianRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface EmployeeBrazilianControlller {
    ResponseEntity<EmployeeResponseDto> createEmployeeBrazilian(EmployeeBrazilianRequestDto employeeBrazilianRequestDto);
    ResponseEntity<Optional<EmployeeResponseDto>> getOneEmployeeBrazilian(String id);
    ResponseEntity<Page<EmployeeResponseDto>> getAllEmployeesBrazilian(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<EmployeeResponseDto>> updateEmployeeBrazilian(EmployeeBrazilianRequestDto employeeBrazilianRequestDto);
    ResponseEntity<Void> deleteEmployeeBrazilian(String id);
}

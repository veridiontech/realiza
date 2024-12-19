package bl.tech.realiza.gateways.controllers.interfaces.employees;

import bl.tech.realiza.gateways.requests.employees.EmployeeBrazilianRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeBrazilianResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface EmployeeBrazilianController {
    ResponseEntity<EmployeeBrazilianResponseDto> createEmployeeBrazilian(EmployeeBrazilianRequestDto branchRequestDto);
    ResponseEntity<Optional<EmployeeBrazilianResponseDto>> getOneEmployeeBrazilian(String id);
    ResponseEntity<Page<EmployeeBrazilianResponseDto>> getAllEmployeesBrazilian(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<EmployeeBrazilianResponseDto>> updateEmployeeBrazilian(EmployeeBrazilianRequestDto branchRequestDto);
    ResponseEntity<Void> deleteEmployeeBrazilian(String id);
}

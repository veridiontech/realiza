package bl.tech.realiza.usecases.interfaces.employees;

import bl.tech.realiza.gateways.requests.employees.EmployeeBrazilianRequestDto;
import bl.tech.realiza.gateways.requests.employees.EmployeeForeignerRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudEmployeeForeigner {
    EmployeeResponseDto save(EmployeeForeignerRequestDto employeeForeignerRequestDto);
    Optional<EmployeeResponseDto> findOne(String id);
    Page<EmployeeResponseDto> findAll(Pageable pageable);
    Optional<EmployeeResponseDto> update(EmployeeForeignerRequestDto employeeForeignerRequestDto);
    void delete(String id);
}

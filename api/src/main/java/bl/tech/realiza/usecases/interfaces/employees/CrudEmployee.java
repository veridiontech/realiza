package bl.tech.realiza.usecases.interfaces.employees;

import bl.tech.realiza.gateways.requests.employees.EmployeeBrazilianRequestDto;
import bl.tech.realiza.gateways.requests.employees.EmployeeForeignerRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudEmployee {
    EmployeeResponseDto saveBrazilian(EmployeeBrazilianRequestDto employeeBrazilianRequestDto);
    EmployeeResponseDto saveForeigner(EmployeeForeignerRequestDto employeeForeignerRequestDto);
    Optional<EmployeeResponseDto> findOne(String id);
    Page<EmployeeResponseDto> findAll(Pageable pageable);
    Optional<EmployeeResponseDto> updateBrazilian(EmployeeBrazilianRequestDto employeeBrazilianRequestDto);
    Optional<EmployeeResponseDto> updateForeigner(EmployeeForeignerRequestDto employeeForeignerRequestDto);
    void delete(String id);
}

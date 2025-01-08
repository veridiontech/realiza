package bl.tech.realiza.usecases.interfaces.employees;

import bl.tech.realiza.gateways.requests.employees.EmployeeBrazilianRequestDto;
import bl.tech.realiza.gateways.requests.employees.EmployeeForeignerRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudEmployeeBrazilian {
    EmployeeResponseDto save(EmployeeBrazilianRequestDto employeeBrazilianRequestDto);
    Optional<EmployeeResponseDto> findOne(String id);
    Page<EmployeeResponseDto> findAll(Pageable pageable);
    Optional<EmployeeResponseDto> update(EmployeeBrazilianRequestDto employeeBrazilianRequestDto);
    void delete(String id);
}

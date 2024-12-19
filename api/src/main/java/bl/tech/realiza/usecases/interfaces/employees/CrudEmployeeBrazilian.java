package bl.tech.realiza.usecases.interfaces.employees;

import bl.tech.realiza.gateways.requests.employees.EmployeeBrazilianRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeBrazilianResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudEmployeeBrazilian {
    EmployeeBrazilianResponseDto save(EmployeeBrazilianRequestDto employeeBrazilianRequestDto);
    Optional<EmployeeBrazilianResponseDto> findOne(String id);
    Page<EmployeeBrazilianResponseDto> findAll(Pageable pageable);
    Optional<EmployeeBrazilianResponseDto> update(EmployeeBrazilianRequestDto employeeBrazilianRequestDto);
    void delete(String id);
}

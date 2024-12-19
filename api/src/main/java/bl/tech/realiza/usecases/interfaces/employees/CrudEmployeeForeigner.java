package bl.tech.realiza.usecases.interfaces.employees;

import bl.tech.realiza.gateways.requests.employees.EmployeeForeignerRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeForeignerResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudEmployeeForeigner {
    EmployeeForeignerResponseDto save(EmployeeForeignerRequestDto employeeForeignerRequestDto);
    Optional<EmployeeForeignerResponseDto> findOne(String id);
    Page<EmployeeForeignerResponseDto> findAll(Pageable pageable);
    Optional<EmployeeForeignerResponseDto> update(EmployeeForeignerRequestDto employeeForeignerRequestDto);
    void delete(String id);
}

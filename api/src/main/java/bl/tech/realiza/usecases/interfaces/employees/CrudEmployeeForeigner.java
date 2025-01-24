package bl.tech.realiza.usecases.interfaces.employees;

import bl.tech.realiza.gateways.requests.employees.EmployeeBrazilianRequestDto;
import bl.tech.realiza.gateways.requests.employees.EmployeeForeignerRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface CrudEmployeeForeigner {
    EmployeeResponseDto save(EmployeeForeignerRequestDto employeeForeignerRequestDto, MultipartFile file) throws IOException;
    Optional<EmployeeResponseDto> findOne(String id);
    Page<EmployeeResponseDto> findAll(Pageable pageable);
    Optional<EmployeeResponseDto> update(String id, EmployeeForeignerRequestDto employeeForeignerRequestDto);
    void delete(String id);
}

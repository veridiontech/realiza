package bl.tech.realiza.usecases.interfaces.employees;

import bl.tech.realiza.gateways.requests.employees.EmployeeBrazilianRequestDto;
import bl.tech.realiza.gateways.requests.employees.EmployeeForeignerRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface CrudEmployeeBrazilian {
    EmployeeResponseDto save(EmployeeBrazilianRequestDto employeeBrazilianRequestDto, MultipartFile file) throws IOException;
    Optional<EmployeeResponseDto> findOne(String id);
    Page<EmployeeResponseDto> findAll(Pageable pageable);
    Optional<EmployeeResponseDto> update(String id, EmployeeBrazilianRequestDto employeeBrazilianRequestDto);
    void delete(String id);
    String changeProfilePicture(String id, MultipartFile file) throws IOException;
}

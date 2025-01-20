package bl.tech.realiza.usecases.interfaces.employees;

import bl.tech.realiza.gateways.requests.services.EmailRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CrudEmployee {
    Page<EmployeeResponseDto> findAllByEnterprise(Pageable pageable, EmailRequestDto.Company company, String idSearch);
}

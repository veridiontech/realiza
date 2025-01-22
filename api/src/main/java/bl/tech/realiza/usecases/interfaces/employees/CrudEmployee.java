package bl.tech.realiza.usecases.interfaces.employees;

import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.gateways.responses.employees.EmployeeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CrudEmployee {
    Page<EmployeeResponseDto> findAllByEnterprise(String idSearch, Provider.Company company, Pageable pageable);
}

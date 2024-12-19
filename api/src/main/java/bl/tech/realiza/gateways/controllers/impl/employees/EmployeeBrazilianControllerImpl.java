package bl.tech.realiza.gateways.controllers.impl.employees;

import bl.tech.realiza.gateways.controllers.interfaces.employees.EmployeeBrazilianController;
import bl.tech.realiza.gateways.requests.employees.EmployeeBrazilianRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeBrazilianResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class EmployeeBrazilianControllerImpl implements EmployeeBrazilianController {
    @Override
    public ResponseEntity<EmployeeBrazilianResponseDto> createEmployeeBrazilian(EmployeeBrazilianRequestDto branchRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<EmployeeBrazilianResponseDto>> getOneEmployeeBrazilian(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Page<EmployeeBrazilianResponseDto>> getAllEmployeesBrazilian(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<EmployeeBrazilianResponseDto>> updateEmployeeBrazilian(EmployeeBrazilianRequestDto branchRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteEmployeeBrazilian(String id) {
        return null;
    }
}

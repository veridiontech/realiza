package bl.tech.realiza.gateways.controllers.impl.employees;

import bl.tech.realiza.gateways.controllers.interfaces.employees.EmployeeForeignerController;
import bl.tech.realiza.gateways.requests.employees.EmployeeForeignerRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeForeignerResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class EmployeeForeignerControllerImpl implements EmployeeForeignerController {
    @Override
    public ResponseEntity<EmployeeForeignerResponseDto> createEmployeeForeigner(EmployeeForeignerRequestDto branchRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<EmployeeForeignerResponseDto>> getOneEmployeeForeigner(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Page<EmployeeForeignerResponseDto>> getAllEmployeesForeigner(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<EmployeeForeignerResponseDto>> updateEmployeeForeigner(EmployeeForeignerRequestDto branchRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteEmployeeForeigner(String id) {
        return null;
    }
}

package bl.tech.realiza.gateways.controllers.impl.employees;

import bl.tech.realiza.gateways.controllers.interfaces.employees.EmployeeForeignerController;
import bl.tech.realiza.gateways.requests.employees.EmployeeForeignerRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeForeignerResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employee/foreign")
public class EmployeeForeignerControllerImpl implements EmployeeForeignerController {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<EmployeeForeignerResponseDto> createEmployeeForeigner(EmployeeForeignerRequestDto branchRequestDto) {
        return null;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<EmployeeForeignerResponseDto>> getOneEmployeeForeigner(String id) {
        return null;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<EmployeeForeignerResponseDto>> getAllEmployeesForeigner(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<EmployeeForeignerResponseDto>> updateEmployeeForeigner(EmployeeForeignerRequestDto branchRequestDto) {
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteEmployeeForeigner(String id) {
        return null;
    }
}

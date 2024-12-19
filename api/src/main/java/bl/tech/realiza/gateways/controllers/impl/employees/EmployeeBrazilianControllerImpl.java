package bl.tech.realiza.gateways.controllers.impl.employees;

import bl.tech.realiza.gateways.controllers.interfaces.employees.EmployeeBrazilianController;
import bl.tech.realiza.gateways.requests.employees.EmployeeBrazilianRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeBrazilianResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employee/brazilian")
public class EmployeeBrazilianControllerImpl implements EmployeeBrazilianController {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<EmployeeBrazilianResponseDto> createEmployeeBrazilian(EmployeeBrazilianRequestDto branchRequestDto) {
        return null;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<EmployeeBrazilianResponseDto>> getOneEmployeeBrazilian(String id) {
        return null;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<EmployeeBrazilianResponseDto>> getAllEmployeesBrazilian(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<EmployeeBrazilianResponseDto>> updateEmployeeBrazilian(EmployeeBrazilianRequestDto branchRequestDto) {
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteEmployeeBrazilian(String id) {
        return null;
    }
}

package bl.tech.realiza.gateways.controllers.impl.employees;

import bl.tech.realiza.gateways.controllers.interfaces.employees.EmployeeBrazilianController;
import bl.tech.realiza.gateways.requests.employees.EmployeeBrazilianRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeBrazilianResponseDto;
import bl.tech.realiza.usecases.impl.employees.CrudEmployeeBrazilianImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employee/brazilian")
public class EmployeeBrazilianControllerImpl implements EmployeeBrazilianController {

    private final CrudEmployeeBrazilianImpl crudEmployeeBrazilian;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<EmployeeBrazilianResponseDto> createEmployeeBrazilian(@RequestBody @Valid EmployeeBrazilianRequestDto employeeBrazilianRequestDto) {
        EmployeeBrazilianResponseDto employeeBrazilian = crudEmployeeBrazilian.save(employeeBrazilianRequestDto);

        return ResponseEntity.of(Optional.of(employeeBrazilian));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<EmployeeBrazilianResponseDto>> getOneEmployeeBrazilian(@PathVariable String id) {
        Optional<EmployeeBrazilianResponseDto> employeeBrazilian = crudEmployeeBrazilian.findOne(id);

        return ResponseEntity.of(Optional.of(employeeBrazilian));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<EmployeeBrazilianResponseDto>> getAllEmployeesBrazilian(@RequestParam(defaultValue = "0") int page,
                                                                                       @RequestParam(defaultValue = "5") int size,
                                                                                       @RequestParam(defaultValue = "id") String sort,
                                                                                       @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<EmployeeBrazilianResponseDto> pageEmployeeBrazilian = crudEmployeeBrazilian.findAll(pageable);

        return ResponseEntity.ok(pageEmployeeBrazilian);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<EmployeeBrazilianResponseDto>> updateEmployeeBrazilian(@RequestBody @Valid EmployeeBrazilianRequestDto employeeBrazilianRequestDto) {
        Optional<EmployeeBrazilianResponseDto> employeeBrazilian = crudEmployeeBrazilian.update(employeeBrazilianRequestDto);

        return ResponseEntity.of(Optional.of(employeeBrazilian));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteEmployeeBrazilian(@PathVariable String id) {
        crudEmployeeBrazilian.delete(id);

        return null;
    }
}

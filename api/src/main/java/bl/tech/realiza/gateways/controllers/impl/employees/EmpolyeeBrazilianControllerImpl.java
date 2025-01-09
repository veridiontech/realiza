package bl.tech.realiza.gateways.controllers.impl.employees;

import bl.tech.realiza.gateways.controllers.interfaces.employees.EmployeeBrazilianControlller;
import bl.tech.realiza.gateways.requests.employees.EmployeeBrazilianRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeResponseDto;
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
public class EmpolyeeBrazilianControllerImpl implements EmployeeBrazilianControlller {

    private final CrudEmployeeBrazilianImpl crudEmployeeBrazilian;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<EmployeeResponseDto> createEmployeeBrazilian(@RequestBody @Valid EmployeeBrazilianRequestDto employeeBrazilianRequestDto) {
        EmployeeResponseDto employeeBrazilian = crudEmployeeBrazilian.save(employeeBrazilianRequestDto);

        return ResponseEntity.of(Optional.of(employeeBrazilian));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<EmployeeResponseDto>> getOneEmployeeBrazilian(@PathVariable String id) {
        Optional<EmployeeResponseDto> employeeBrazilian = crudEmployeeBrazilian.findOne(id);

        return ResponseEntity.of(Optional.of(employeeBrazilian));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<EmployeeResponseDto>> getAllEmployeesBrazilian(@RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "5") int size,
                                                                              @RequestParam(defaultValue = "id") String sort,
                                                                              @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<EmployeeResponseDto> pageEmployeeBrazilian = crudEmployeeBrazilian.findAll(pageable);

        return ResponseEntity.ok(pageEmployeeBrazilian);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<EmployeeResponseDto>> updateEmployeeBrazilian(@RequestBody @Valid EmployeeBrazilianRequestDto employeeBrazilianRequestDto) {
        Optional<EmployeeResponseDto> employeeBrazilian = crudEmployeeBrazilian.update(employeeBrazilianRequestDto);

        return ResponseEntity.of(Optional.of(employeeBrazilian));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteEmployeeBrazilian(@PathVariable String id) {
        crudEmployeeBrazilian.delete(id);

        return null;
    }
}

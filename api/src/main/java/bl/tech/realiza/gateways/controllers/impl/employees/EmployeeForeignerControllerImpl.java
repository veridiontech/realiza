package bl.tech.realiza.gateways.controllers.impl.employees;

import bl.tech.realiza.gateways.controllers.interfaces.employees.EmployeeForeignerController;
import bl.tech.realiza.gateways.requests.employees.EmployeeForeignerRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeForeignerResponseDto;
import bl.tech.realiza.usecases.impl.employees.CrudEmployeeForeignerImpl;
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
@RequestMapping("/employee/foreign")
public class EmployeeForeignerControllerImpl implements EmployeeForeignerController {

    private final CrudEmployeeForeignerImpl crudEmployeeForeigner;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<EmployeeForeignerResponseDto> createEmployeeForeigner(@RequestBody @Valid EmployeeForeignerRequestDto employeeForeignerRequestDto) {
        EmployeeForeignerResponseDto employeeForeigner = crudEmployeeForeigner.save(employeeForeignerRequestDto);

        return ResponseEntity.of(Optional.of(employeeForeigner));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<EmployeeForeignerResponseDto>> getOneEmployeeForeigner(@PathVariable String id) {
        Optional<EmployeeForeignerResponseDto> employeeForeigner = crudEmployeeForeigner.findOne(id);

        return ResponseEntity.of(Optional.of(employeeForeigner));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<EmployeeForeignerResponseDto>> getAllEmployeesForeigner(@RequestParam(defaultValue = "0") int page,
                                                                                       @RequestParam(defaultValue = "5") int size,
                                                                                       @RequestParam(defaultValue = "id") String sort,
                                                                                       @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<EmployeeForeignerResponseDto> pageEmployeeForeigner = crudEmployeeForeigner.findAll(pageable);

        return ResponseEntity.ok(pageEmployeeForeigner);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<EmployeeForeignerResponseDto>> updateEmployeeForeigner(@RequestBody @Valid  EmployeeForeignerRequestDto employeeForeignerRequestDto) {
        Optional<EmployeeForeignerResponseDto> employeeForeigner = crudEmployeeForeigner.update(employeeForeignerRequestDto);

        return ResponseEntity.of(Optional.of(employeeForeigner));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Void> deleteEmployeeForeigner(@PathVariable String id) {
        crudEmployeeForeigner.delete(id);

        return null;
    }
}

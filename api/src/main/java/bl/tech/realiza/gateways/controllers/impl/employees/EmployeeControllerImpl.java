package bl.tech.realiza.gateways.controllers.impl.employees;

import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.gateways.controllers.interfaces.employees.EmployeeController;
import bl.tech.realiza.gateways.requests.employees.EmployeeBrazilianRequestDto;
import bl.tech.realiza.gateways.requests.employees.EmployeeForeignerRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeResponseDto;
import bl.tech.realiza.gateways.responses.services.PageResponse;
import bl.tech.realiza.usecases.impl.employees.CrudEmployeeBrazilianImpl;
import bl.tech.realiza.usecases.impl.employees.CrudEmployeeForeignerImpl;
import bl.tech.realiza.usecases.impl.employees.CrudEmployeeImpl;
import bl.tech.realiza.usecases.interfaces.employees.CrudEmployee;
import bl.tech.realiza.usecases.interfaces.employees.CrudEmployeeBrazilian;
import bl.tech.realiza.usecases.interfaces.employees.CrudEmployeeForeigner;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employee")
@Tag(name = "Employee")
public class EmployeeControllerImpl implements EmployeeController {

    private final CrudEmployeeBrazilian crudEmployeeBrazilian;
    private final CrudEmployeeForeigner crudEmployeeForeigner;
    private final CrudEmployee crudEmployee;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<PageResponse<EmployeeResponseDto>> getAllEmployeesByEnterprise(@RequestParam(defaultValue = "0") int page,
                                                                                         @RequestParam(defaultValue = "5") int size,
                                                                                         @RequestParam(defaultValue = "name") String sort,
                                                                                         @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                                         @RequestParam Provider.Company enterprise,
                                                                                         @RequestParam String idSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort, "surname"));

        Page<EmployeeResponseDto> employeeResponseDtos = crudEmployee.findAllByEnterprise(idSearch, enterprise, pageable);

        return ResponseEntity.ok(new PageResponse<>(employeeResponseDtos));
    }

    @GetMapping("/filtered-by-contract")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<EmployeeResponseDto>> getAllEmployeesByContract(@RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "5") int size,
                                                                               @RequestParam(defaultValue = "name") String sort,
                                                                               @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                               @RequestParam String idContract) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort, "surname"));

        Page<EmployeeResponseDto> pageEmployeeForeigner = crudEmployee. findAllByContract(idContract, pageable);

        return ResponseEntity.ok(pageEmployeeForeigner);
    }

    @PostMapping("/brazilian")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<EmployeeResponseDto> createEmployeeBrazilian(@RequestBody @Valid EmployeeBrazilianRequestDto employeeBrazilianRequestDto) {

        return ResponseEntity.of(Optional.of(crudEmployeeBrazilian.save(employeeBrazilianRequestDto)));
    }

    @GetMapping("/brazilian/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<EmployeeResponseDto>> getOneEmployeeBrazilian(@PathVariable String id) {
        Optional<EmployeeResponseDto> employeeBrazilian = crudEmployeeBrazilian.findOne(id);

        return ResponseEntity.of(Optional.of(employeeBrazilian));
    }

    @GetMapping("/brazilian")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<EmployeeResponseDto>> getAllEmployeesBrazilian(@RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "5") int size,
                                                                              @RequestParam(defaultValue = "name") String sort,
                                                                              @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort, "surname"));

        Page<EmployeeResponseDto> pageEmployeeBrazilian = crudEmployeeBrazilian.findAll(pageable);

        return ResponseEntity.ok(pageEmployeeBrazilian);
    }

    @PutMapping(value = "/brazilian/{id}", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<EmployeeResponseDto>> updateEmployeeBrazilian(@PathVariable String id,
                                                                                 @RequestPart("employeeBrazilianRequestDto") @Valid EmployeeBrazilianRequestDto employeeBrazilianRequestDto) {
        Optional<EmployeeResponseDto> employeeBrazilian = null;
        try {
            employeeBrazilian = crudEmployeeBrazilian.update(id, employeeBrazilianRequestDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.of(Optional.of(employeeBrazilian));
    }


    @PatchMapping("/brazilian/change-profile-picture/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<String> updateEmployeeBrazilianProfilePicture(@PathVariable String id, @RequestPart(value = "file") MultipartFile file) {
        String employeBrazilian = null;
        try {
            employeBrazilian = crudEmployeeBrazilian.changeProfilePicture(id, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(employeBrazilian);
    }

    @DeleteMapping("/brazilian/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteEmployeeBrazilian(@PathVariable String id) {
        crudEmployeeBrazilian.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping( "/foreigner")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<EmployeeResponseDto> createEmployeeForeigner(@RequestBody @Valid EmployeeForeignerRequestDto employeeForeignerRequestDto) {
        EmployeeResponseDto employeeForeigner = null;
        try {
            employeeForeigner = crudEmployeeForeigner.save(employeeForeignerRequestDto);
        } catch (Exception e) {
            throw new RuntimeException("Error saving employee",e);
        }

        return ResponseEntity.of(Optional.of(employeeForeigner));
    }

    @GetMapping("/foreigner/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<EmployeeResponseDto>> getOneEmployeeForeigner(@PathVariable String id) {
        Optional<EmployeeResponseDto> employeeForeigner = crudEmployeeForeigner.findOne(id);

        return ResponseEntity.of(Optional.of(employeeForeigner));
    }

    @GetMapping("/foreigner")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<EmployeeResponseDto>> getAllEmployeesForeigner(@RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "5") int size,
                                                                              @RequestParam(defaultValue = "name") String sort,
                                                                              @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort, "surname"));

        Page<EmployeeResponseDto> pageEmployeeForeigner = crudEmployeeForeigner.findAll(pageable);

        return ResponseEntity.ok(pageEmployeeForeigner);
    }

    @PutMapping(value = "/foreigner/{id}", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<EmployeeResponseDto>> updateEmployeeForeigner(@PathVariable String id,
                                                                                 @RequestBody @Valid EmployeeForeignerRequestDto employeeForeignerRequestDto) {
        Optional<EmployeeResponseDto> employeeForeigner = null;
        try {
            employeeForeigner = crudEmployeeForeigner.update(id, employeeForeignerRequestDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.of(Optional.of(employeeForeigner));
    }


    @PatchMapping("/foreigner/change-profile-picture/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<String> updateEmployeeForeignerProfilePicture(@PathVariable String id, @RequestPart(value = "file") MultipartFile file) {
        String employeeForeigner = null;
        try {
            employeeForeigner = crudEmployeeForeigner.changeProfilePicture(id, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(employeeForeigner);
    }

    @DeleteMapping("/foreigner/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteEmployeeForeigner(@PathVariable String id) {
        crudEmployeeForeigner.delete(id);

        return ResponseEntity.noContent().build();
    }
}

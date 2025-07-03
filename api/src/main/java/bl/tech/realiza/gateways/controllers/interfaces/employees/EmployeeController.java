package bl.tech.realiza.gateways.controllers.interfaces.employees;

import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.gateways.requests.employees.EmployeeBrazilianRequestDto;
import bl.tech.realiza.gateways.requests.employees.EmployeeForeignerRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeResponseDto;
import bl.tech.realiza.gateways.responses.services.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface EmployeeController {
    ResponseEntity<PageResponse<EmployeeResponseDto>> getAllEmployeesByEnterprise(int page, int size, String sort, Sort.Direction direction, Provider.Company company, String idSearch);
    ResponseEntity<Page<EmployeeResponseDto>> getAllEmployeesByContract(int page, int size, String sort, Sort.Direction direction, String idContract);
    ResponseEntity<EmployeeResponseDto> changeEmployeeSituation(String employeeId, Employee.Situation situation);
    ResponseEntity<Boolean> checkEmployeeStatus(String employeeId);

    // brazilian
    ResponseEntity<EmployeeResponseDto> createEmployeeBrazilian(EmployeeBrazilianRequestDto employeeBrazilianRequestDto);
    ResponseEntity<Optional<EmployeeResponseDto>> getOneEmployeeBrazilian(String id);
    ResponseEntity<Page<EmployeeResponseDto>> getAllEmployeesBrazilian(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<EmployeeResponseDto>> updateEmployeeBrazilian(String id, EmployeeBrazilianRequestDto employeeBrazilianRequestDto);
    ResponseEntity<String> updateEmployeeBrazilianProfilePicture(String id, MultipartFile file);
    ResponseEntity<Void> deleteEmployeeBrazilian(String id);

    // foreigner
    ResponseEntity<EmployeeResponseDto> createEmployeeForeigner(EmployeeForeignerRequestDto employeeForeignerRequestDto);
    ResponseEntity<Optional<EmployeeResponseDto>> getOneEmployeeForeigner(String id);
    ResponseEntity<Page<EmployeeResponseDto>> getAllEmployeesForeigner(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<EmployeeResponseDto>> updateEmployeeForeigner(String id, EmployeeForeignerRequestDto employeeForeignerRequestDto);
    ResponseEntity<String> updateEmployeeForeignerProfilePicture(String id, MultipartFile file);
    ResponseEntity<Void> deleteEmployeeForeigner(String id);
}

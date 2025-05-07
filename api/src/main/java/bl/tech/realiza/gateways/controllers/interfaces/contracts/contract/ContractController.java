package bl.tech.realiza.gateways.controllers.interfaces.contracts.contract;

import bl.tech.realiza.gateways.requests.contracts.EmployeeToContractRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractByEmployeeResponseDto;
import bl.tech.realiza.gateways.responses.contracts.ContractResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ContractController {
    ResponseEntity<String> finishContract(String idContract);
    ResponseEntity<String> addEmployeeToContract(String idContract, EmployeeToContractRequestDto employeeToContractRequestDto);
    ResponseEntity<String> removeEmployeeFromContract(String idContract, EmployeeToContractRequestDto employeeToContractRequestDto);
    ResponseEntity<Page<ContractByEmployeeResponseDto>> getContractsByEmployee(int page, int size, String sort, Sort.Direction direction, String idEmployee);
}

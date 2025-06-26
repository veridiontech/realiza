package bl.tech.realiza.usecases.interfaces.contracts.contract;

import bl.tech.realiza.gateways.requests.contracts.EmployeeToContractRequestDto;
import bl.tech.realiza.gateways.responses.contracts.contract.ContractByEmployeeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CrudContract {
    String finishContract(String idContract);
    String suspendContract(String contractId);
    String addEmployeeToContract(String idContract, EmployeeToContractRequestDto employeeToContractRequestDto);
    String removeEmployeeToContract(String idContract, EmployeeToContractRequestDto employeeToContractRequestDto);
    Page<ContractByEmployeeResponseDto> getContractByEmployee(Pageable pageable, String idEmployee);
}

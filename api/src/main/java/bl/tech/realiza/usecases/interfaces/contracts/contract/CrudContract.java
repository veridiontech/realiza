package bl.tech.realiza.usecases.interfaces.contracts.contract;

import bl.tech.realiza.gateways.requests.contracts.EmployeeToContractRequestDto;
import bl.tech.realiza.gateways.responses.contracts.contract.ContractByBranchIdsResponseDto;
import bl.tech.realiza.gateways.responses.contracts.contract.ContractByEmployeeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CrudContract {
    String finishContract(String idContract);
    String suspendContract(String contractId);
    String reactivateContract(String contractId);
    String addEmployeeToContract(String idContract, EmployeeToContractRequestDto employeeToContractRequestDto);
    String removeEmployeeToContract(String idContract, EmployeeToContractRequestDto employeeToContractRequestDto);
    Page<ContractByEmployeeResponseDto> getContractByEmployee(Pageable pageable, String idEmployee);
    List<ContractByBranchIdsResponseDto> getContractByBranchIds(List<String> branchIds);
}

package bl.tech.realiza.gateways.controllers.interfaces.contracts.contract;

import org.springframework.http.ResponseEntity;

public interface ContractController {
    ResponseEntity<String> finishContract(String idContract);
    ResponseEntity<String> addEmployeeToContract(String idContract, String idEmployee);
    ResponseEntity<String> removeEmployeeToContract(String idContract, String idEmployee);
}

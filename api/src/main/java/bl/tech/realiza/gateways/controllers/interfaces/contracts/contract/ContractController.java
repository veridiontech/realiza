package bl.tech.realiza.gateways.controllers.interfaces.contracts.contract;

import org.springframework.http.ResponseEntity;

public interface ContractController {
    ResponseEntity<String> finishContract(String idContract);
}

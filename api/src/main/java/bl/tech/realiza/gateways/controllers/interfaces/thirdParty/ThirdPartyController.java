package bl.tech.realiza.gateways.controllers.interfaces.thirdParty;

import bl.tech.realiza.gateways.requests.services.LoginRequestDto;
import org.springframework.http.ResponseEntity;

public interface ThirdPartyController {
    ResponseEntity<Boolean> checkEmployeeByClient(String employeeCpf, LoginRequestDto request);
}

package bl.tech.realiza.gateways.controllers.interfaces.users;

import org.springframework.http.ResponseEntity;

public interface UserController {
    ResponseEntity<String> userActivation(String userId, Boolean activation);
}

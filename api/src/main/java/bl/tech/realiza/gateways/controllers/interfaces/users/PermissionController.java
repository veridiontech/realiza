package bl.tech.realiza.gateways.controllers.interfaces.users;

import bl.tech.realiza.gateways.requests.users.security.CreatePermissionRequest;
import bl.tech.realiza.gateways.responses.users.profile.PermissionResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PermissionController {
    ResponseEntity<PermissionResponse> save(CreatePermissionRequest request);
    ResponseEntity<PermissionResponse> findOne(String id);
    ResponseEntity<List<PermissionResponse>> findAll();
}

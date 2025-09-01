package bl.tech.realiza.usecases.interfaces.users.security;

import bl.tech.realiza.gateways.requests.users.security.CreatePermissionRequest;
import bl.tech.realiza.gateways.responses.users.profile.PermissionResponse;

import java.util.List;

public interface CrudPermission {
    PermissionResponse save(CreatePermissionRequest request);
    PermissionResponse findOne(String id);
    List<PermissionResponse> findAll();
}

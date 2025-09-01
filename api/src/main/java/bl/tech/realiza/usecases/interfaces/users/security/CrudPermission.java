package bl.tech.realiza.usecases.interfaces.users.security;

import bl.tech.realiza.domains.enums.DocumentTypeEnum;
import bl.tech.realiza.domains.enums.PermissionSubTypeEnum;
import bl.tech.realiza.domains.enums.PermissionTypeEnum;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.gateways.requests.users.security.CreatePermissionRequest;
import bl.tech.realiza.gateways.responses.users.profile.PermissionResponse;

import java.util.List;

public interface CrudPermission {
    PermissionResponse save(CreatePermissionRequest request);
    PermissionResponse findOne(String id);
    List<PermissionResponse> findAll();
    Boolean hasPermission(User user, PermissionTypeEnum permissionTypeEnum, PermissionSubTypeEnum permissionSubTypeEnum, DocumentTypeEnum documentTypeEnum);
}

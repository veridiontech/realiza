package bl.tech.realiza.gateways.requests.users.security;

import bl.tech.realiza.domains.enums.DocumentTypeEnum;
import bl.tech.realiza.domains.enums.PermissionSubTypeEnum;
import bl.tech.realiza.domains.enums.PermissionTypeEnum;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreatePermissionRequest {
    @NotEmpty
    private PermissionTypeEnum type;
    @NotEmpty
    private PermissionSubTypeEnum subType;
    @NotEmpty
    private DocumentTypeEnum documentType;
}

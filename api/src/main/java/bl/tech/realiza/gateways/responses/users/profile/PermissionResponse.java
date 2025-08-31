package bl.tech.realiza.gateways.responses.users.profile;

import bl.tech.realiza.domains.enums.DocumentTypeEnum;
import bl.tech.realiza.domains.enums.PermissionSubTypeEnum;
import bl.tech.realiza.domains.enums.PermissionTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PermissionResponse {
    private String id;
    private String name;
    private PermissionTypeEnum type;
    private PermissionSubTypeEnum subType;
    private DocumentTypeEnum documentType;
}

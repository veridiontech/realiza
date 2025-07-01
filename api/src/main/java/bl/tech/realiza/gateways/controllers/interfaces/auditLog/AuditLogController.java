package bl.tech.realiza.gateways.controllers.interfaces.auditLog;

import bl.tech.realiza.domains.enums.AuditLogActionsEnum;
import bl.tech.realiza.domains.enums.AuditLogTypeEnum;
import bl.tech.realiza.gateways.responses.auditLog.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

public interface AuditLogController {
    ResponseEntity<Page<AuditLogResponseDto>> getAuditLog(String id, AuditLogActionsEnum action, AuditLogTypeEnum auditLogTypeEnum, String userId, int page, int size, String sort, Sort.Direction direction);
}



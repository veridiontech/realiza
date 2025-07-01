package bl.tech.realiza.gateways.controllers.interfaces.auditLog;

import bl.tech.realiza.domains.enums.AuditLogActions;
import bl.tech.realiza.domains.enums.AuditLogType;
import bl.tech.realiza.gateways.responses.auditLog.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

public interface AuditLogController {
    ResponseEntity<Page<AuditLogResponseDto>> getAuditLog(String id, AuditLogActions action, AuditLogType auditLogType, String userId, int page, int size, String sort, Sort.Direction direction);
}



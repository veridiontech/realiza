package bl.tech.realiza.usecases.interfaces.auditLogs;

import bl.tech.realiza.domains.enums.AuditLogActionsEnum;
import bl.tech.realiza.domains.enums.AuditLogTypeEnum;
import bl.tech.realiza.gateways.responses.auditLog.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

public interface AuditLogService {
    @Transactional
    @Async
    void createAuditLog(String id, AuditLogTypeEnum typeEnum, String description, String justification, String notes, AuditLogActionsEnum action, String userResponsibleId);

    Page<AuditLogResponseDto> getAuditLogs(String id, AuditLogActionsEnum action, AuditLogTypeEnum auditLogTypeEnum, String userId, Pageable pageable);
}

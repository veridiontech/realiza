package bl.tech.realiza.gateways.controllers.impl.auditLog;

import bl.tech.realiza.domains.auditLogs.activity.AuditLogActivity;
import bl.tech.realiza.domains.auditLogs.contract.AuditLogContract;
import bl.tech.realiza.domains.auditLogs.document.AuditLogDocument;
import bl.tech.realiza.domains.auditLogs.employee.AuditLogEmployee;
import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogBranch;
import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogClient;
import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogProvider;
import bl.tech.realiza.domains.auditLogs.serviceType.AuditLogServiceType;
import bl.tech.realiza.domains.auditLogs.user.AuditLogUser;
import bl.tech.realiza.domains.enums.AuditLogActions;
import bl.tech.realiza.domains.enums.AuditLogType;
import bl.tech.realiza.gateways.controllers.interfaces.auditLog.AuditLogController;
import bl.tech.realiza.gateways.responses.auditLog.*;
import bl.tech.realiza.usecases.impl.auditLogs.AuditLogServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/audit-log")
@Tag(name = "Audit Log")
public class AuditLogControllerImpl implements AuditLogController {

    private final AuditLogServiceImpl auditLogServiceImpl;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<AuditLogResponseDto>> getAuditLog(@RequestParam String id,
                                                                 @RequestParam AuditLogActions action,
                                                                 @RequestParam AuditLogType auditLogType,
                                                                 @RequestParam String userId,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(defaultValue = "createdAt") String sort,
                                                                 @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));
        return ResponseEntity.ok(auditLogServiceImpl.getAuditLogs(id, action, auditLogType, pageable));
    }
}

package bl.tech.realiza.gateways.controllers.impl.auditLog;

import bl.tech.realiza.domains.enums.AuditLogActionsEnum;
import bl.tech.realiza.domains.enums.AuditLogTypeEnum;
import bl.tech.realiza.gateways.controllers.interfaces.auditLog.AuditLogController;
import bl.tech.realiza.gateways.responses.auditLog.*;
import bl.tech.realiza.usecases.interfaces.auditLogs.AuditLogService;
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

    private final AuditLogService auditLogServiceImpl;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<AuditLogResponseDto>> getAuditLog(@RequestParam String id,
                                                                 @RequestParam AuditLogActionsEnum action,
                                                                 @RequestParam AuditLogTypeEnum auditLogTypeEnum,
                                                                 @RequestParam String userId,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(defaultValue = "createdAt") String sort,
                                                                 @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));
        return ResponseEntity.ok(auditLogServiceImpl.getAuditLogs(id, action, auditLogTypeEnum, pageable));
    }
}

package bl.tech.realiza.gateways.repositories.auditLogs.contract;

import bl.tech.realiza.domains.auditLogs.contract.AuditLogContract;
import bl.tech.realiza.domains.enums.AuditLogActionsEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogContractRepository extends JpaRepository<AuditLogContract, String> {
    Page<AuditLogContract> findAllByContractId(String id, Pageable pageable);
    Page<AuditLogContract> findAllByContractIdAndAction(String id, AuditLogActionsEnum action, Pageable pageable);
}

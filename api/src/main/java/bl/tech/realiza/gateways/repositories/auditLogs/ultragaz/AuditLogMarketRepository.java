package bl.tech.realiza.gateways.repositories.auditLogs.ultragaz;

import bl.tech.realiza.domains.auditLogs.ultragaz.AuditLogMarket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogMarketRepository extends JpaRepository<AuditLogMarket, String> {
}

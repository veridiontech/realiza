package bl.tech.realiza.gateways.repositories.auditLogs.dashboard;

import bl.tech.realiza.domains.auditLogs.dashboard.DocumentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DocumentStatusHistoryRepository extends JpaRepository<DocumentStatusHistory, String>, JpaSpecificationExecutor<DocumentStatusHistory> {

}

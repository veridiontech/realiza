package bl.tech.realiza.domains.auditLogs.contract;

import bl.tech.realiza.domains.auditLogs.AuditLog;
import bl.tech.realiza.domains.contract.Contract;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("CONTRACT")
public class AuditLogContract extends AuditLog {
    private String contractId;
    private String contractReference;
    private String responsibleId;
    private String responsibleFullName;
    private String clientId;
    private String clientCorporateName;
    private String branchId;
    private String branchName;
    private String supplierId;
    private String supplierCorporateName;
    private String subcontractorId;
    private String subcontractorCorporateName;
}

package bl.tech.realiza.domains.services.dashboardSnapshot.general;

import bl.tech.realiza.domains.services.dashboardSnapshot.DashboardSnapshot;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("GENERAL")
public class DashboardSnapshotGeneral extends DashboardSnapshot {
    private Long supplierQuantity;
    private Long contractQuantity;
    private Long allocatedEmployeeQuantity;
    private Double conformity;

    private List<DashboardSnapshotGeneralTypeStatus> documentStatus;
    private List<DashboardSnapshotGeneralExemption> documentExemption;
    private List<DashboardSnapshotGeneralPending> pendingRanking;
}

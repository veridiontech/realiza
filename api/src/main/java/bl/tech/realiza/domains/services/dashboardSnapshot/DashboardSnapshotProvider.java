package bl.tech.realiza.domains.services.dashboardSnapshot;

import bl.tech.realiza.domains.enums.ConformityLevel;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@DiscriminatorValue("PROVIDER")
public class DashboardSnapshotProvider extends DashboardSnapshot {
    private String corporateName;
    private String cnpj;
    private Long totalDocumentQuantity;
    private Long adherenceQuantity;
    private Long nonAdherenceQuantity;
    private Long conformityQuantity;
    private Long nonConformityQuantity;
    private Double adherence;
    private Double conformity;
    @Enumerated(EnumType.STRING)
    private ConformityLevel conformityRange;
}

package bl.tech.realiza.domains.services.dashboardSnapshot.general;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DashboardSnapshotGeneralTypeStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private String name;
    private List<DashboardSnapshotGeneralStatus> status;
}

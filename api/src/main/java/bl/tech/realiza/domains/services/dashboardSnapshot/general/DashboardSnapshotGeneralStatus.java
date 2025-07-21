package bl.tech.realiza.domains.services.dashboardSnapshot.general;

import bl.tech.realiza.domains.documents.Document;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DashboardSnapshotGeneralStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private Document.Status status;
    private Integer quantity;
}

package bl.tech.realiza.domains.auditLogs.dashboard;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.providers.Provider;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.YearMonth;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DocumentStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Long totalDocuments;
    private Long adherent;
    private Long conformity;
    @Column(nullable = false)
    private YearMonth historyPeriod;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "idProvider")
    @JsonManagedReference
    private Provider provider;
}

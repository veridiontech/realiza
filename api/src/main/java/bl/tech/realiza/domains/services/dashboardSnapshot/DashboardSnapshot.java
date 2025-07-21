package bl.tech.realiza.domains.services.dashboardSnapshot;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.enums.ConformityLevel;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type")
public abstract class DashboardSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private String client;

    // filtros
    private String branch;
    private String responsible;
    private String documentType;
    private Contract.IsActive isActive;
    private Document.Status documentStatus;
    private String documentTitle;
}

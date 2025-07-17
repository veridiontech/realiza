package bl.tech.realiza.domains.services;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.providers.Provider;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
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
public class DashboardSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "clientId")
    @JsonBackReference
    private Client client;

    @ManyToOne
    @JoinColumn(name = "branchId")
    @JsonBackReference
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "providerId")
    @JsonBackReference
    private Provider provider;

    private String documentType;
    private Document.Status documentStatus;
}

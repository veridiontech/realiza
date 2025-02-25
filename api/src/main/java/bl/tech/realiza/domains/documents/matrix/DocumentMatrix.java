package bl.tech.realiza.domains.documents.matrix;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentClient;
import bl.tech.realiza.domains.employees.Employee;
import jakarta.persistence.*;
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
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class DocumentMatrix {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idDocument;
    private String name;
    private String risk;
    private String expiration;
    private String type;
    private String doesBlock;
    @Builder.Default
    private Boolean isActive = true;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "idDocumentSubgroup", nullable = false)
    private DocumentMatrixSubgroup subGroup;

    @OneToMany(mappedBy = "documentMatrix", cascade = CascadeType.DETACH, orphanRemoval = true)
    private List<Document> documents;
}

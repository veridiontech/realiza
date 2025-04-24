package bl.tech.realiza.domains.contract.activity;

import bl.tech.realiza.domains.documents.client.DocumentBranch;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"idActivity", "idDocumentation"}))
public class ActivityDocuments {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Builder.Default
    private Boolean isSelected = false;

    @ManyToOne
    @JoinColumn(name = "idActivity")
    private Activity activity;

    @ManyToOne
    @JoinColumn(name = "idDocumentation")
    private DocumentBranch documentBranch;
}

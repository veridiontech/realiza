package bl.tech.realiza.domains.contract.activity;

import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"idActivity", "idDocument"}))
public class ActivityDocumentsRepo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "idActivity")
    private ActivityRepo activity;

    @ManyToOne
    @JoinColumn(name = "idDocument")
    private DocumentMatrix documentMatrix;
}

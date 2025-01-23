package bl.tech.realiza.domains.documents.matrix;

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
public class DocumentMatrixGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idDocumentGroup;
    private String groupName;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
}

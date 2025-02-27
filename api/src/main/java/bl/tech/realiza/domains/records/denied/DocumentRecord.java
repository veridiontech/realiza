package bl.tech.realiza.domains.records.denied;

import bl.tech.realiza.domains.documents.Document;
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
public abstract class DocumentRecord {
    /*
    descomplicar os responses
    criar crud
    criar controller
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idDocumentRecord;
    private Document.Status status;
    private String reason;
    private String documentation;
    @Builder.Default
    private LocalDateTime versionDate = LocalDateTime.now();
}

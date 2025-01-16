package bl.tech.realiza.domains.documents.matrix;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DocumentMatrix {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idDocument;
    private String risk;
    private String expiration;
    private String type;
    private String doesBlock;
    private Date creationDate;
    @ManyToOne
    private DocumentMatrixSubgroup subGroup;
}

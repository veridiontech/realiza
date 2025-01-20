package bl.tech.realiza.domains.documents.matrix;

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
    private LocalDateTime creationDate;
    @ManyToOne(cascade = CascadeType.REMOVE)
    private DocumentMatrixSubgroup subGroup;

    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDateTime.now();
    }
}

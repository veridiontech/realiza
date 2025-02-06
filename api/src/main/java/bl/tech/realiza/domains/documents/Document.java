package bl.tech.realiza.domains.documents;

import bl.tech.realiza.domains.clients.Client;
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
@DiscriminatorColumn(name = "documentation_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idDocumentation;
    private String title;
    private String status;
    private String documentation;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime versionDate;
    private LocalDateTime expirationDate;
    @Builder.Default
    private Boolean deleteRequest = false;

    @ManyToOne(cascade = CascadeType.REMOVE)
    private Client documentInMatrix;
}

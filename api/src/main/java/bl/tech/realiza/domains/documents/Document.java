package bl.tech.realiza.domains.documents;

import bl.tech.realiza.domains.clients.Client;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.sql.Date;

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
    private Date creationDate;
    private Date versionDate;
    private Date expirationDate;
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne
    private Client documentInMatrix;
}

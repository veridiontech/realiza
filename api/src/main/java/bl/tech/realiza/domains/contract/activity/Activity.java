package bl.tech.realiza.domains.contract.activity;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idActivity;
    private String title;
    private Risk risk;
    @Builder.Default
    private Boolean deleteRequest = false;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    public enum Risk {
        LOW,
        MEDIUM,
        HIGH,
        VERY_HIGH
    }

    // -------------------------------
    // Relacionamentos INERENTES
    // -------------------------------
    @ManyToOne
    @JoinColumn(name = "idBranch")
    private Branch branch;

    @JsonIgnore
    @ManyToMany(mappedBy = "activities")
    private List<Contract> contracts;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityDocuments> documentAssociations;
}

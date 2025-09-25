package bl.tech.realiza.domains.services.snapshots.user;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.contract.ContractSnapshot;
import bl.tech.realiza.domains.services.snapshots.ids.SnapshotId;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "APP_USER_SNAPSHOT")
public class UserSnapshot {
    @EmbeddedId
    private SnapshotId id;
    private String firstName;
    private String surname;
    private String email;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    @OneToMany(mappedBy = "responsible", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<ContractSnapshot> contracts;

    public String getFullName() {
        return String.format("%s %s", this.firstName != null ? this.firstName : "", this.surname != null ? this.surname : "").trim();
    }
}

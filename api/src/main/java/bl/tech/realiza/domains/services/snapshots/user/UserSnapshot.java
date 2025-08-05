package bl.tech.realiza.domains.services.snapshots.user;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.contract.ContractSnapshot;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "APP_USER_SNAPSHOT")
public class UserSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String firstName;
    private String surname;
    private String email;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    private SnapshotFrequencyEnum frequency;

    @OneToMany(mappedBy = "responsible")
    @JsonBackReference
    private List<ContractSnapshot> contracts;

    public String getFullName() {
        return String.format("%s %s", this.firstName != null ? this.firstName : "", this.surname != null ? this.surname : "").trim();
    }
}

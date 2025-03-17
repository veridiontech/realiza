package bl.tech.realiza.domains.contract;

import bl.tech.realiza.domains.clients.Branch;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
        HIGH
    }

    @ManyToOne
    @JoinColumn(name = "idBranch")
    private Branch branch;

    @JsonIgnore
    @OneToMany(mappedBy = "activity", cascade = CascadeType.DETACH, orphanRemoval = true)
    private List<Contract> contracts;

}

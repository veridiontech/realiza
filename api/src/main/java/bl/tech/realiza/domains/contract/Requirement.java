package bl.tech.realiza.domains.contract;

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
public class Requirement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idRequirement;
    private String title;
    @Builder.Default
    private Boolean deleteRequest = false;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    @JsonIgnore
    @ManyToMany(mappedBy = "requirements")
    private List<Contract> contracts;
}

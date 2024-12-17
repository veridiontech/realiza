package bl.tech.realiza.domains.contracts;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @ManyToMany
    private List<Requirement> requirements;
}

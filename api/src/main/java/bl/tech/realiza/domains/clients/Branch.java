package bl.tech.realiza.domains.clients;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idBranch;
    private String name;
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne
    private Client client;
}

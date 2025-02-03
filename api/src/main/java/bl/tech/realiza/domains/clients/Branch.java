package bl.tech.realiza.domains.clients;

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
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idBranch;
    private String name;
    @Builder.Default
    private Boolean isActive = false;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    @ManyToOne(cascade = CascadeType.REMOVE)
    private Client client;
}

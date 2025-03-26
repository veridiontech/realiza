package bl.tech.realiza.domains.ultragaz;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrixSubgroup;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Center {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idCenter;
    private String name;

    // -------------------------------
    // Relacionamentos INERENTES
    // -------------------------------
    @ManyToOne
    @JoinColumn(name = "idMarket")
    private Market market;

    // -------------------------------
    // Relacionamentos CONTRATUAIS
    // -------------------------------
    @JsonIgnore
    @OneToMany(mappedBy = "center", cascade = CascadeType.REMOVE)
    private List<Branch> branches;
}

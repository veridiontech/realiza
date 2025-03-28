package bl.tech.realiza.domains.ultragaz;

import bl.tech.realiza.domains.clients.Client;
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
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idBoard;
    private String name;

    // -------------------------------
    // Relacionamentos INERENTES
    // -------------------------------
    @ManyToOne
    @JoinColumn(name = "idClient")
    private Client client;

    // -------------------------------
    // Relacionamentos CONTRATUAIS
    // -------------------------------
    @JsonIgnore
    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<Market> markets;
}

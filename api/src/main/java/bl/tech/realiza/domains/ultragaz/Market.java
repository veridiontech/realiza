package bl.tech.realiza.domains.ultragaz;

import bl.tech.realiza.domains.clients.Branch;
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
public class Market {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idMarket;
    private String name;

    @ManyToOne
    @JoinColumn(name = "idBoard", nullable = false)
    private Board board;

    @JsonIgnore
    @OneToMany(mappedBy = "market", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Center> centers;
}

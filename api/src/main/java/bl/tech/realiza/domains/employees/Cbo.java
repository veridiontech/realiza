package bl.tech.realiza.domains.employees;

import bl.tech.realiza.domains.clients.Branch;
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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"code", "title"}))
public class Cbo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String code;
    private String title;

    @JsonIgnore
    @OneToMany(mappedBy = "cbo")
    private List<Employee> employees;
}

package bl.tech.realiza.domains.contract;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.employees.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "CONTRACT_EMPLOYEE",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_contract_employee_unique",
                        columnNames = {"idContract", "idEmployee"}
                )
        }
)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ContractEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private Boolean integrated = false;

    // 🔗 Relacionamento com Contract
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idContract", nullable = false)
    @EqualsAndHashCode.Include
    private Contract contract;

    // 🔗 Relacionamento com Employee
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idEmployee", nullable = false)
    @EqualsAndHashCode.Include
    private Employee employee;
}

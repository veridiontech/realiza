package bl.tech.realiza.domains.services.snapshots.documents.employee;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.documents.DocumentSnapshot;
import bl.tech.realiza.domains.services.snapshots.employees.EmployeeSnapshot;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@DiscriminatorValue("EMPLOYEE")
public class DocumentEmployeeSnapshot extends DocumentSnapshot {
    @Builder.Default
    private LocalDateTime assignmentDate = LocalDateTime.now();
    private SnapshotFrequencyEnum frequency;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "employeeId", referencedColumnName = "id"),
            @JoinColumn(name = "employeeFrequency", referencedColumnName = "frequency"),
            @JoinColumn(name = "employeeSnapshotDate", referencedColumnName = "snapshotDate")
    })
    @JsonManagedReference
    private EmployeeSnapshot employee;
}

package bl.tech.realiza.domains.contract.serviceType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "service_type")
public abstract class ServiceType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idServiceType;
    private String title;
    private Risk risk;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    public enum Risk {
        LOW,
        MEDIUM,
        HIGH,
        VERY_HIGH
    }
}

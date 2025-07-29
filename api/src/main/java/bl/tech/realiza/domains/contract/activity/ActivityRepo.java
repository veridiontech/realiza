package bl.tech.realiza.domains.contract.activity;

import bl.tech.realiza.domains.enums.RiskEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class ActivityRepo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idActivity;
    private String title;
    private RiskEnum risk;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
}

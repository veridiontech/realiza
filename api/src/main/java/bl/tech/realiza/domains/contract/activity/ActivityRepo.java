package bl.tech.realiza.domains.contract.activity;

import bl.tech.realiza.domains.enums.RiskEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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

    @OneToMany
    @JsonBackReference
    private List<Activity> activities;
}

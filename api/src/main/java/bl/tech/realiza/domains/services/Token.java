package bl.tech.realiza.domains.services;

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
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String enterpriseName;
    private String enterpriseCnpj;
    @Column(length = 1000)
    private String token;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

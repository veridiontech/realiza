package bl.tech.realiza.gateways.responses.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DashboardProviderDetailsResponseDto {
    private String corporateName;
    private String cnpj;
    private Long totalDocumentQuantity;
    private Long adherenceQuantity;
    private Long nonAdherenceQuantity;
    private Long conformityQuantity;
    private Long nonConformityQuantity;
    private Double adherence;
    private Double conformity;
    private Conformity conformityRange;
    private Long employeeQuantity;

    public enum Conformity {
        RISKY,
        ATTENTION,
        NORMAL,
        OK
    }
}

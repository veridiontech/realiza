package bl.tech.realiza.gateways.responses.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
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

    public enum Conformity {
        RISKY,
        ATTENTION,
        NORMAL,
        OK
    }
}

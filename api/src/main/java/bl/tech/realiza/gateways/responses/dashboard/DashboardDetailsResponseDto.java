package bl.tech.realiza.gateways.responses.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DashboardDetailsResponseDto {
    private List<TypeStatus> documentStatus;
    private List<Exemption> documentExemption;
    private List<Pending> pendingRanking;

    @Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class TypeStatus {
        private String name;
        private List<Status> status;
    }

    @Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Status {
        private String type;
        private Integer quantity;
    }

    @Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Exemption {
        private String name;
        private Integer quantity;
    }

    @Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Pending {
        private String corporateName;
        private String cnpj;
        private Double adherence;
        private Double conformity;
        private Integer nonConformingDocumentQuantity;
        private Conformity conformityLevel;
    }

    public enum Conformity {
        RISKY,
        ATTENTION,
        NORMAL,
        OK
    }
}

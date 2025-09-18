package bl.tech.realiza.gateways.responses.dashboard;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.enums.RiskLevel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DashboardGeneralDetailsResponseDto {
    private Long supplierQuantity;
    private Long contractQuantity;
    private Long allocatedEmployeeQuantity;
    private Double conformity;
    private List<TypeStatus> documentStatus;
    private List<Exemption> documentExemption;
    private List<Pending> pendingRanking;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class TypeStatus {
        private String name;
        private List<Status> status;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Status {
        private Document.Status status;
        private Integer quantity;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Exemption {
        private String name;
        private Integer quantity;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Pending {
        private String corporateName;
        private String cnpj;
        private Double adherence;
        private Integer nonAdherentDocumentQuantity;
        private RiskLevel adherenceLevel;
        private Double conformity;
        private Integer nonConformingDocumentQuantity;
        private RiskLevel conformityLevel;
    }
}

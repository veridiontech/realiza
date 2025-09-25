package bl.tech.realiza.gateways.responses.dashboard;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.enums.RiskLevel;
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
public class DashboardGeneralDetailsResponseDto {
    private Long supplierQuantity;
    private Long contractQuantity;
    private Long allocatedEmployeeQuantity;
    private Double conformity;
    private Long totalDocuments;
    private Long adherent;
    private Long conforming;
    private Long nonAdherent;
    private Long nonConforming;
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
        private Document.Status status;
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
        private Integer nonAdherentDocumentQuantity;
        private RiskLevel adherenceLevel;
        private Double conformity;
        private Integer nonConformingDocumentQuantity;
        private RiskLevel conformityLevel;
        private Long employeeQuantity;
    }
}

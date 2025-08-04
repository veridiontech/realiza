package bl.tech.realiza.gateways.responses.dashboard;

import bl.tech.realiza.domains.documents.Document;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DashboardDocumentResponseDto {
    private Long adherentDocumentsQuantity;
    private Long nonAdherentDocumentsQuantity;
    private Long conformingDocumentsQuantity;
    private Long nonConformingDocumentsQuantity;
    private List<Status> documentStatus;
    private List<DocumentDto> documentList;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Status {
        private Document.Status status;
        private Boolean adherent;
        private Boolean conforming;
        private Long quantity;
        private Double percentage;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class DocumentDto {
        private String branchName;
        private String supplierName;
        private String supplierCnpj;
        private String contractType;
        private Boolean adherent;
        private Boolean conforming;
        private Long quantity;
        private Double percentage;
    }
}

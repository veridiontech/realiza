package bl.tech.realiza.gateways.responses.dashboard;

import bl.tech.realiza.domains.documents.Document;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DashboardDocumentResponseDto {
    private Long adherentDocumentsQuantity;
    private Long nonAdherentDocumentsQuantity;
    private Long conformingDocumentsQuantity;
    private Long nonConformingDocumentsQuantity;
    private Status documentStatus;

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
}

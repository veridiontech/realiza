package bl.tech.realiza.gateways.responses.documents;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.Document.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ContractDocumentAndEmployeeResponseDto {
    private String enterpriseName;
    private List<DocumentDto> documentDtos;
    private List<EmployeeDto> employeeDtos;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class DocumentDto {
        private String id;
        private String title;
        private String type;
        private Status status;
        private String ownerName;
        private Boolean enterprise;
        private Boolean hasDoc;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class EmployeeDto {
        private String id;
        private String name;
        private String cboTitle;
    }
}

package bl.tech.realiza.gateways.responses.api.document.employee;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DocumentEmployeeExternalApiResponseDto {
    private String cpf;
    private String fullName;
    private Boolean status;
    private DocumentsNotApproved documentsNotApproved;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class DocumentsNotApproved {
        private String title;
        private String status;
    }
}

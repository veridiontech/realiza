package bl.tech.realiza.gateways.responses.contracts.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ContractResponsibleResponseDto {
    private List<ContractResponsibleInfosResponseDto> contracts;
    private List<ResponsibleResponseDto> responsibleList;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ContractResponsibleInfosResponseDto {
        private String contractId;
        private String contractReference;
        private String responsibleId;
        private String responsibleFullName;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ResponsibleResponseDto {
        private String responsibleId;
        private String responsibleFullName;
    }
}

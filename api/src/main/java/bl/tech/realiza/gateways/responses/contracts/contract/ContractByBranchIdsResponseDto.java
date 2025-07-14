package bl.tech.realiza.gateways.responses.contracts.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ContractByBranchIdsResponseDto {
    private String id;
    private String contractReference;
    private String branchId;
    private String branchName;
}

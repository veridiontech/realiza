package bl.tech.realiza.gateways.responses.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DashboardHomeResponseDto {
    private double adherence;
    private Integer activeContractQuantity;
    private Integer activeEmployeeQuantity;
}

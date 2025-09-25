package bl.tech.realiza.gateways.responses.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DashboardHomeResponseDto {
    private double conformity;
    private Integer activeContractQuantity;
    private Integer activeEmployeeQuantity;
    private Integer supplierQuantity;
    private Integer allocatedEmployeeQuantity;
}

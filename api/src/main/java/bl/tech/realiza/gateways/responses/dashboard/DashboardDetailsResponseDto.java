package bl.tech.realiza.gateways.responses.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DashboardDetailsResponseDto {
    public Double adherence;
}

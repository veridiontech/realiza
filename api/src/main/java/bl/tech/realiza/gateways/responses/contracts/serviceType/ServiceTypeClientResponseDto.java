package bl.tech.realiza.gateways.responses.contracts.serviceType;

import bl.tech.realiza.domains.contract.serviceType.ServiceType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ServiceTypeClientResponseDto {
    private String idServiceType;
    private String title;
    private ServiceType.Risk risk;
    private String clientId;
    private LocalDateTime createdAt;
}

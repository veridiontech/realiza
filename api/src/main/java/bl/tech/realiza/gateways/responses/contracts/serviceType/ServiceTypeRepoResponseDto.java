package bl.tech.realiza.gateways.responses.contracts.serviceType;

import bl.tech.realiza.domains.contract.serviceType.ServiceType;
import bl.tech.realiza.domains.enums.RiskEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ServiceTypeRepoResponseDto {
    private String idServiceType;
    private String title;
    private RiskEnum risk;
    private LocalDateTime createdAt;
}

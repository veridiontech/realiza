package bl.tech.realiza.gateways.responses.clients.controlPanel.service;

import bl.tech.realiza.domains.enums.RiskEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import static bl.tech.realiza.domains.contract.serviceType.ServiceType.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ServiceTypeControlPanelResponseDto {
    private String id;
    private String title;
    private RiskEnum risk;
}

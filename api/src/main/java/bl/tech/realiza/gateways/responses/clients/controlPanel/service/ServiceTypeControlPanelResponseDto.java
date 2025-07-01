package bl.tech.realiza.gateways.responses.clients.controlPanel.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import static bl.tech.realiza.domains.contract.serviceType.ServiceType.*;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ServiceTypeControlPanelResponseDto {
    private String id;
    private String title;
    private Risk risk;
}

package bl.tech.realiza.gateways.responses.clients.controlPanel.service;

import bl.tech.realiza.domains.enums.RiskEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

import static bl.tech.realiza.domains.contract.serviceType.ServiceType.*;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ServiceTypeRiskControlPanelResponseDto {
    private RiskEnum risk;
    private List<ServiceTypeControlPanelResponseDto> services;
}

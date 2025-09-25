package bl.tech.realiza.gateways.responses.clients.controlPanel;

import bl.tech.realiza.gateways.responses.clients.controlPanel.activity.ActivityRiskControlPanelResponseDto;
import bl.tech.realiza.gateways.responses.clients.controlPanel.document.DocumentTypeControlPanelResponseDto;
import bl.tech.realiza.gateways.responses.clients.controlPanel.service.ServiceTypeRiskControlPanelResponseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ControlPanelResponseDto {
    private List<DocumentTypeControlPanelResponseDto> documents;
    private List<ActivityRiskControlPanelResponseDto> activities;
    private List<ServiceTypeRiskControlPanelResponseDto> services;
}

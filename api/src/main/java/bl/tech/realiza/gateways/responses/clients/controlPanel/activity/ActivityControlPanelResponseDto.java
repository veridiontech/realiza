package bl.tech.realiza.gateways.responses.clients.controlPanel.activity;

import bl.tech.realiza.domains.enums.RiskEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import static bl.tech.realiza.domains.contract.activity.Activity.*;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ActivityControlPanelResponseDto {
    private String id;
    private String title;
    private RiskEnum risk;
}
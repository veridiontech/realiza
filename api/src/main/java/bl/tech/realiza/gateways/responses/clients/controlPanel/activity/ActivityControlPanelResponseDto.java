package bl.tech.realiza.gateways.responses.clients.controlPanel.activity;

import bl.tech.realiza.domains.enums.RiskEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import static bl.tech.realiza.domains.contract.activity.Activity.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ActivityControlPanelResponseDto {
    private String id;
    private String title;
    private RiskEnum risk;
}
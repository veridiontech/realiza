package bl.tech.realiza.gateways.responses.clients.controlPanel.document;

import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DocumentControlPanelResponseDto {
    private String id;
    private String title;
    private Integer expirationQuantity;
    private DocumentMatrix.DayUnitEnum expirationDateUnit;
    private String type;
}

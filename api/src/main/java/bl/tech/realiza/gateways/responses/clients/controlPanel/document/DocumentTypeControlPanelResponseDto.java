package bl.tech.realiza.gateways.responses.clients.controlPanel.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DocumentTypeControlPanelResponseDto {
    private String typeName;
    private List<DocumentControlPanelResponseDto> documents;
}

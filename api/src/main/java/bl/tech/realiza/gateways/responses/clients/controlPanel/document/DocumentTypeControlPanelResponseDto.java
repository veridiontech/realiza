package bl.tech.realiza.gateways.responses.clients.controlPanel.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DocumentTypeControlPanelResponseDto {
    private String typeName;
    private List<DocumentControlPanelResponseDto> documents;
}

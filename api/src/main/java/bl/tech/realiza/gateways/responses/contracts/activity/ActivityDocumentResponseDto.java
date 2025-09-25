package bl.tech.realiza.gateways.responses.contracts.activity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ActivityDocumentResponseDto {
    private String idAssociation;
    private String idDocument;
    private String idActivity;
    private String documentTitle;
    private Boolean selected;
}

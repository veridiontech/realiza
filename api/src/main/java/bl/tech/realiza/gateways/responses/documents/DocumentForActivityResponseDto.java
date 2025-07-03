package bl.tech.realiza.gateways.responses.documents;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DocumentForActivityResponseDto {
    private String idDocumentation;
    private String title;
    private String type;
}

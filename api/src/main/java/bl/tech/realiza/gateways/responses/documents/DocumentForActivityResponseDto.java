package bl.tech.realiza.gateways.responses.documents;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DocumentForActivityResponseDto {
    private String idDocumentation;
    private String title;
    private String type;
}

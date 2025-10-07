package bl.tech.realiza.gateways.responses.documents;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class DocumentSummarizedWithSelectionResponseDto {
    private String idDocument;
    private String title;
    private Boolean selected;
}

package bl.tech.realiza.gateways.responses.documents;

import lombok.AllArgsConstructor;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentSummarizedResponseDto {
    private String idDocument;
    private String title;
}

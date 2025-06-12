package bl.tech.realiza.gateways.responses.documents;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentSummarizedResponseDto {
    private String idDocument;
    private String title;
}

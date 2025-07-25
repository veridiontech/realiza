package bl.tech.realiza.gateways.responses.documents;

import bl.tech.realiza.domains.documents.Document;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DocumentPendingResponseDto {
    private String id;
    private String title;
    private Document.Status status;
    private String owner;
    private List<String> contractReferences;
    private String signedUrl;
}

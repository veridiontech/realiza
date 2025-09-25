package bl.tech.realiza.gateways.responses.records.denied;

import bl.tech.realiza.domains.documents.Document;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DocumentRecordSubcontractorResponseDto {
    private String idDocumentRecord;
    private Document.Status status;
    private String reason;
    private String documentation;
    private String subcontractor;
}

package bl.tech.realiza.gateways.responses.records.denied;

import bl.tech.realiza.domains.documents.Document;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DocumentRecordSupplierResponseDto {
    private String idDocumentRecord;
    private Document.Status status;
    private String reason;
    private String documentation;
    private String supplier;
}

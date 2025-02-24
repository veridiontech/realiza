package bl.tech.realiza.gateways.requests.records.denied;

import bl.tech.realiza.domains.documents.Document;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DocumentRecordSupplierRequestDto {
    @NotEmpty
    private Document.Status status;
    private String reason;
    @NotEmpty
    private String documentation;
    @NotEmpty
    private String supplier;
}

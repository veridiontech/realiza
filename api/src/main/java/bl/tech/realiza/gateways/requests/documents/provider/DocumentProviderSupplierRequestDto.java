package bl.tech.realiza.gateways.requests.documents.provider;

import bl.tech.realiza.domains.documents.Document;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Date;

@Data
public class DocumentProviderSupplierRequestDto {
    private String title;
    private String risk;
    private Document.Status status;
    private String documentation;
    private String type;
    private Date creationDate;
    private Date versionDate;
    private Date expirationDate;
    private String supplier;
}

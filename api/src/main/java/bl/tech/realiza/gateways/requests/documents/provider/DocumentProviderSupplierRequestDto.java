package bl.tech.realiza.gateways.requests.documents.provider;

import bl.tech.realiza.domains.documents.Document;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private String documentMatrixId;
}

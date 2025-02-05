package bl.tech.realiza.gateways.requests.documents.provider;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Date;

@Data
public class DocumentProviderSupplierRequestDto {
    private String title;
    private String risk;
    private String status;
    private String documentation;
    private String type;
    private Date creationDate;
    private Date versionDate;
    private Date expirationDate;
    private String supplier;
}

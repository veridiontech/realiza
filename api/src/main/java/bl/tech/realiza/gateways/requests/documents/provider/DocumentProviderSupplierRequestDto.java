package bl.tech.realiza.gateways.requests.documents.provider;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Date;

@Data
public class DocumentProviderSupplierRequestDto {
    private String idDocumentation;
    @NotEmpty
    private String title;
    @NotEmpty
    private String risk;
    @NotEmpty
    private String status;
    @NotEmpty
    private String documentation;
    private String type;
    @NotNull
    private Date creationDate;
    private Date versionDate;
    private Date expirationDate;
    @NotEmpty
    private String supplier;
    private Boolean isActive;
}

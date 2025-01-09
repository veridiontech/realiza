package bl.tech.realiza.gateways.requests.documents.provider;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Date;

@Data
public class DocumentProviderSupplierRequestDto {
    private String id_documentation;
    @NotEmpty
    private String title;
    @NotEmpty
    private String risk;
    @NotEmpty
    private String status;
    @NotEmpty
    private String documentation;
    @NotNull
    private Date creation_date;
    @NotEmpty
    private String supplier;
}

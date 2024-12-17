package bl.tech.realiza.gateways.requests.documents.client;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class DocumentsClientRequestDto {
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
    private String client;
}

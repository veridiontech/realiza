package bl.tech.realiza.gateways.requests.documents.contract;

import bl.tech.realiza.domains.documents.Document;
import lombok.Data;

import java.sql.Date;

@Data
public class DocumentContractRequestDto {
    private String title;
    private String risk;
    private Document.Status status;
    private String documentation;
    private String type;
    private Date versionDate;
    private Date expirationDate;
    private String contract;
}

package bl.tech.realiza.gateways.requests.documents.client;

import lombok.Data;

import java.sql.Date;

@Data
public class DocumentBranchRequestDto {
    private String idDocumentation;
    private String title;
    private String risk;
    private String status;
    private String documentation;
    private String type;
    private Date versionDate;
    private Date expirationDate;
    private String branch;
    private Boolean isActive;
}

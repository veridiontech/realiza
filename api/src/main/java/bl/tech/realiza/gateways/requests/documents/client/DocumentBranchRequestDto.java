package bl.tech.realiza.gateways.requests.documents.client;

import bl.tech.realiza.domains.documents.Document;
import lombok.*;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentBranchRequestDto {
    private String title;
    private String matrixDocumentId;
    private Document.Status status;
    private String documentation;
    private String type;
    private Date expirationDate;
    private String branch;
}

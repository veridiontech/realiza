package bl.tech.realiza.gateways.requests.documents.employee;

import bl.tech.realiza.domains.documents.Document;
import lombok.*;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentEmployeeRequestDto {
    private String title;
    private String risk;
    private Document.Status status;
    private String documentation;
    private String type;
    private Date versionDate;
    private Date expirationDate;
    private String employee;
}

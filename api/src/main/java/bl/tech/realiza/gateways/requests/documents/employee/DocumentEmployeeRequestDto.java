package bl.tech.realiza.gateways.requests.documents.employee;

import lombok.Data;

import java.sql.Date;

@Data
public class DocumentEmployeeRequestDto {
    private String title;
    private String risk;
    private String status;
    private String documentation;
    private String type;
    private Date versionDate;
    private Date expirationDate;
    private String employee;
}

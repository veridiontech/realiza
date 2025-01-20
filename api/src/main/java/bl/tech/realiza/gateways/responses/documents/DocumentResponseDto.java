package bl.tech.realiza.gateways.responses.documents;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DocumentResponseDto {
    // document
    private String idDocumentation;
    private String title;
    private String status;
    private String documentation;
    private String fileName;
    private String fileContentType;
    private byte[] fileData;
    private LocalDateTime creationDate;
    private Date versionDate;
    private Date expirationDate;

    // branch
    private String branch;

    // client
    private String client;

    // employee
    private String employee;

    // subcontractor
    private String subcontractor;

    // supplier
    private String supplier;
}

package bl.tech.realiza.gateways.responses.documents.employee;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DocumentsEmployeeResponseDto {
    private String id_documentation;
    private String title;
    private String risk;
    private String status;
    private String documentation;
    private Date creation_date;
    private String employee;
}

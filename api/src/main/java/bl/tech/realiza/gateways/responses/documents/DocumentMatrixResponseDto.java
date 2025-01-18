package bl.tech.realiza.gateways.responses.documents;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DocumentMatrixResponseDto {
    private String idDocumentMatrix;
    private String name;
    private String risk;
    private String expiration;
    private String type;
    private String doesBlock;
    private String idDocumentSubgroup;
    private String subgroupName;
    private String idDocumentGroup;
    private String groupName;
}

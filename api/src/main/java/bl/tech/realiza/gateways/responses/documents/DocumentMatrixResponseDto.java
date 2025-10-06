package bl.tech.realiza.gateways.responses.documents;

import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DocumentMatrixResponseDto {
    private String documentId;
    private String idDocumentMatrix;
    private String name;
    private DocumentMatrix.DayUnitEnum expirationDateUnit;
    private Integer expirationDateAmount;
    private String type;
    private Boolean doesBlock;
    private Boolean required;
    private Boolean isDocumentUnique;
    private Boolean isValidityFixed;
    private String fixedValidityAt;
    private String idDocumentSubgroup;
    private String subgroupName;
    private String idDocumentGroup;
    private String groupName;
}

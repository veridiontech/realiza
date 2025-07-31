package bl.tech.realiza.gateways.requests.documents.matrix;

import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import lombok.Data;

@Data
public class DocumentMatrixRequestDto {
    private String name;
    private String type;
    private Boolean doesBlock;
    private Boolean isDocumentUnique;
    private String subgroup;
    private DocumentMatrix.DayUnitEnum expirationDateUnit;
    private Integer expirationDateAmount;
}

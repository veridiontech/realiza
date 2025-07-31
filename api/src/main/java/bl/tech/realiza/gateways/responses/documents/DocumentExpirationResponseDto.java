package bl.tech.realiza.gateways.responses.documents;

import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DocumentExpirationResponseDto {
    private String idDocument;
    private String title;
    private Integer expirationDateAmount;
    private DocumentMatrix.DayUnitEnum expirationDateDayUnitEnum;
}

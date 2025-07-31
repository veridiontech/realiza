package bl.tech.realiza.gateways.requests.documents.client;

import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import lombok.Data;

@Data
public class DocumentExpirationUpdateRequestDto {
    private Integer expirationDateAmount;
    private DocumentMatrix.DayUnitEnum expirationDateDayUnitEnum;
}

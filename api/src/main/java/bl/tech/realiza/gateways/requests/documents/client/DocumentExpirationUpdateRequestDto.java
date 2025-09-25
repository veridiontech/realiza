package bl.tech.realiza.gateways.requests.documents.client;

import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentExpirationUpdateRequestDto {
    private Integer expirationDateAmount;
    private DocumentMatrix.DayUnitEnum expirationDateUnit;
    private Boolean doesBlock;
}

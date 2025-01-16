package bl.tech.realiza.gateways.requests.documents.matrix;

import lombok.Data;

@Data
public class DocumentMatrixRequestDto {
    private String idDocumentMatrix;
    private String risk;
    private String expiration;
    private String type;
    private String doesBlock;
    private String subGroup;
}

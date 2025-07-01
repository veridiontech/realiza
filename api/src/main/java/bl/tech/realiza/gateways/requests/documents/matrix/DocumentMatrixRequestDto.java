package bl.tech.realiza.gateways.requests.documents.matrix;

import lombok.Data;

@Data
public class DocumentMatrixRequestDto {
    private String name;
    private String risk;
    private String expiration;
    private String type;
    private Boolean doesBlock;
    private String subgroup;
}

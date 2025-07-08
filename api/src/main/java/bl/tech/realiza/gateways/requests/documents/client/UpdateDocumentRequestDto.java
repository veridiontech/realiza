package bl.tech.realiza.gateways.requests.documents.client;

import lombok.Data;

import java.util.List;

@Data
public class UpdateDocumentRequestDto {
    private List<String> documentIds;
    private List<String> branchIds;
}

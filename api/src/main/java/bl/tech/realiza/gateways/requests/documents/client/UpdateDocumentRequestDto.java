package bl.tech.realiza.gateways.requests.documents.client;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDocumentRequestDto {
    private List<String> documentIds;
    private List<String> branchIds;
}

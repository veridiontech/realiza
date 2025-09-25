package bl.tech.realiza.gateways.requests.services.itemManagement;

import bl.tech.realiza.domains.services.ItemManagement;
import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemManagementDocumentRequestDto {
    private ItemManagement.SolicitationType solicitationType;
    private String idRequester;
    @Column(length = 1000)
    private String description;
    private String documentId;
    private String contractId;
}

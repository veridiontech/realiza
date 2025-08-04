package bl.tech.realiza.gateways.requests.services.itemManagement;

import bl.tech.realiza.domains.services.ItemManagement;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemManagementUserRequestDto {
    private ItemManagement.SolicitationType solicitationType;
    private String idRequester;
    private String idNewUser;
}

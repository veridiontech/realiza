package bl.tech.realiza.gateways.requests.services.itemManagement;

import bl.tech.realiza.domains.services.ItemManagement;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemManagementProviderRequestDto {
    private ItemManagement.SolicitationType solicitationType;
    private String idRequester;
    private String idNewProvider;
}

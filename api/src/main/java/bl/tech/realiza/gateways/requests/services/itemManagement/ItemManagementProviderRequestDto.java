package bl.tech.realiza.gateways.requests.services.itemManagement;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemManagementProviderRequestDto {
    private String title;
    private String details;
    private String idRequester;
    private String idNewProvider;
}

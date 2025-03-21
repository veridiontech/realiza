package bl.tech.realiza.gateways.requests.services.itemManagement;

import lombok.Data;

@Data
public class ItemManagementUserRequestDto {
    private String title;
    private String details;
    private String idRequester;
    private String idNewUser;
}

package bl.tech.realiza.gateways.requests.services;

import lombok.Data;

@Data
public class ItemManagementRequestDto {
    private String title;
    private String details;
    private String idRequester;
    private String idNewUser;
}

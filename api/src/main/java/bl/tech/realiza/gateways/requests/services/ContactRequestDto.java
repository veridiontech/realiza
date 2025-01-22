package bl.tech.realiza.gateways.requests.services;

import lombok.Data;

@Data
public class ContactRequestDto {
    private String idContact;
    private String department;
    private String email;
    private String country;
    private String telephone;
    private Boolean mainContact;
    private String client;
    private Boolean isActive;
}

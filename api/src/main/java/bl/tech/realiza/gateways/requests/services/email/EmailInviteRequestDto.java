package bl.tech.realiza.gateways.requests.services.email;

import bl.tech.realiza.domains.providers.Provider;
import lombok.Data;

@Data
public class EmailInviteRequestDto {
    private String email;
    private Provider.Company company;
    private String idCompany;
}

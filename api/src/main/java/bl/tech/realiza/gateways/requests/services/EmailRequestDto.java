package bl.tech.realiza.gateways.requests.services;

import bl.tech.realiza.domains.providers.Provider;
import lombok.Data;

@Data
public class EmailRequestDto {
    private String email;
    private Provider.Company company;
    private String idCompany;
}

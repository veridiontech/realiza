package bl.tech.realiza.gateways.requests.services.email;

import bl.tech.realiza.domains.providers.Provider;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailInviteRequestDto {
    private String email;
    private Provider.Company company; // o nível da empresa que quer criar
    private String idCompany; // onde vai se linkar
    private String idClient;
}

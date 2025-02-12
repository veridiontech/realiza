package bl.tech.realiza.gateways.requests.services.email;

import bl.tech.realiza.domains.providers.Provider;
import lombok.Data;

@Data
public class EmailInviteRequestDto {
    private String email;
    private Provider.Company company; // o nível da empresa que quer criar
    private String idCompany; // o id do "pai" onde vai se ligar
}

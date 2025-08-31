package bl.tech.realiza.gateways.requests.users.security;

import lombok.Data;

@Data
public class ProfileRepoRequestDto {
    private String name;
    private String description;
    private Boolean admin;
    private ProfileRequestDto.DashboardRequest dashboard;
    private ProfileRequestDto.DocumentRequest document;
    private ProfileRequestDto.ContractRequest contract;
    private Boolean reception;
}

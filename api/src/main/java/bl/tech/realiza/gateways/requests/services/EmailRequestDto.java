package bl.tech.realiza.gateways.requests.services;

import lombok.Data;

@Data
public class EmailRequestDto {
    private String email;
    private Company company;
    private String idCompany;

    public enum Company {
        CLIENT,
        SUPPLIER,
        SUBCONTRACTOR
    }
}

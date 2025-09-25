package bl.tech.realiza.gateways.requests.services;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequestDto {
    private String department;
    private String email;
    private String country;
    private String telephone;
    private Boolean mainContact;
    private String client;
}

package bl.tech.realiza.gateways.responses.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ContactResponseDto {
    private String idContact;
    private String department;
    private String email;
    private String country;
    private String telephone;
    private Boolean mainContact;
    private String branch;
}

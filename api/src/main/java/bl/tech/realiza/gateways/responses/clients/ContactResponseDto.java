package bl.tech.realiza.gateways.responses.clients;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ContactResponseDto {
    private String idContact;
    private String department;
    private String email;
    private String country;
    private String telephone;
    private Boolean mainContact;
    private String client;
}

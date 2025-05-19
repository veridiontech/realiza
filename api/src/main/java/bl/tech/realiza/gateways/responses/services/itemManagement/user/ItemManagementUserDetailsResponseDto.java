package bl.tech.realiza.gateways.responses.services.itemManagement.user;

import bl.tech.realiza.domains.services.ItemManagement;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ItemManagementUserDetailsResponseDto {
    private String idSolicitation;

    private ItemManagement.SolicitationType solicitationType;
    private LocalDateTime creationDate;

    private Client client;
    private Requester requester;
    private NewUser newUser;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Client {
        private String cnpj;
        private String tradeName;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class NewUser {
        private String fullName;
        private String cpf;
        private String email;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Requester {
        private String fullName;
        private String email;
    }
}

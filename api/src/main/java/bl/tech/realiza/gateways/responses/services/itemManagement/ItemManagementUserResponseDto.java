package bl.tech.realiza.gateways.responses.services.itemManagement;

import bl.tech.realiza.domains.services.ItemManagement;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ItemManagementUserResponseDto {
    private String idSolicitation;
    private String title;
    private String details;
    private ItemManagement.Status status;
    private LocalDateTime creationDate;
    private Requester requester;
    private NewUser newUser;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class NewUser {
        private String idUser;
        private String firstName;
        private String surname;
        private String cpf;
        private String email;
        private String enterprise;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Requester {
        private String idUser;
        private String firstName;
        private String surname;
        private String cpf;
        private String email;
        private String nameEnterprise;
    }
}

package bl.tech.realiza.gateways.responses.services.itemManagement.contract;

import bl.tech.realiza.domains.services.ItemManagement;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ItemManagementContractDetailsResponseDto {
    private String idSolicitation;

    private ItemManagement.SolicitationType solicitationType;
    private LocalDateTime creationDate;

    private Client client;
    private Enterprise enterprise;
    private Requester requester;
    private Contract contract;

    @Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Client {
        private String cnpj;
        private String corporateName;
    }

    @Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Enterprise {
        private String corporateName;
    }

    @Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Contract {
        private String contractReference;
    }

    @Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Requester {
        private String fullName;
        private String email;
    }
}

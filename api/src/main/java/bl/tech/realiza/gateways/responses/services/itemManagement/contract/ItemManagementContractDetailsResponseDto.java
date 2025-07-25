package bl.tech.realiza.gateways.responses.services.itemManagement.contract;

import bl.tech.realiza.domains.services.ItemManagement;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
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

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Client {
        private String cnpj;
        private String corporateName;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Enterprise {
        private String corporateName;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Contract {
        private String contractReference;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Requester {
        private String fullName;
        private String email;
    }
}

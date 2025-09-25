package bl.tech.realiza.gateways.responses.services.itemManagement.provider;

import bl.tech.realiza.domains.services.ItemManagement;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ItemManagementProviderDetailsResponseDto {
    private String idSolicitation;

    private ItemManagement.SolicitationType solicitationType;
    private LocalDateTime creationDate;

    private Client client;
    private Requester requester;
    private NewProvider newProvider;

    @Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Client {
        private String cnpj;
        private String tradeName;
    }

    @Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class NewProvider {
        private String cnpj;
        private String corporateName;
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

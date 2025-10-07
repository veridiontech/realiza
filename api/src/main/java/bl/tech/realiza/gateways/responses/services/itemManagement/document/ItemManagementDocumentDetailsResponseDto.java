package bl.tech.realiza.gateways.responses.services.itemManagement.document;

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
public class ItemManagementDocumentDetailsResponseDto {
    private String idSolicitation;

    private ItemManagement.SolicitationType solicitationType;
    private LocalDateTime creationDate;

    private Client client;
    private Enterprise enterprise;
    private Requester requester;
    private Document document;

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
        private String cnpj;
        private String corporateName;
    }

    @Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Document {
        private String title;
        private String ownerName;
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

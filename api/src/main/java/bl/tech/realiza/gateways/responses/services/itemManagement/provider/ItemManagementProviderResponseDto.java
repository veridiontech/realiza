package bl.tech.realiza.gateways.responses.services.itemManagement.provider;

import bl.tech.realiza.domains.services.ItemManagement;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ItemManagementProviderResponseDto {
    private String idSolicitation;
    private String enterpriseName; // nome da empresa

    private ItemManagement.SolicitationType solicitationType; // criação/inativação
    private String clientName; // nome do cliente na qual essa empresa pertence
    private String clientCnpj; // cnpj do cliente

    private String requesterName; // nome do requirente
    private String requesterEmail; // email do requirente

    private ItemManagement.Status status; // status
    private LocalDateTime creationDate; // data e horário da solicitação
}

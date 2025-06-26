package bl.tech.realiza.gateways.responses.services.itemManagement.user;

import bl.tech.realiza.domains.services.ItemManagement;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ItemManagementUserResponseDto {
    private String idSolicitation;
    private String userFullName; // nome do usuário

    private ItemManagement.SolicitationType solicitationType; // criação/inativação
    private String clientTradeName; // nome do cliente na qual essa usuário pertence
    private String clientCnpj; // cnpj do cliente
    private String branchName; // nome da filial

    private String requesterFullName; // nome do requirente
    private String requesterEmail; // email do requirente

    private ItemManagement.Status status; // status
    private LocalDateTime creationDate; // data e horário da solicitação
}

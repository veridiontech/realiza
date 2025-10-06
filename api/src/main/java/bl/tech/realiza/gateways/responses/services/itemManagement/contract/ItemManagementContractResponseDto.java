package bl.tech.realiza.gateways.responses.services.itemManagement.contract;

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
public class ItemManagementContractResponseDto {
    private String idSolicitation;
    private String contractReference; // referencia do contrato
    private String enterpriseName; // nome da empresado dono do contrato
    private String enterpriseCnpj; // cnpj da empresado dono do contrato

    private ItemManagement.SolicitationType solicitationType; // ação
    private String clientName; // nome do cliente na qual essa empresa pertence
    private String clientCnpj; // cnpj do cliente
    private String branchName; // nome da filial
    private String branchCnpj; // cnpj da filial

    private String requesterName; // nome do requirente
    private String requesterEmail; // email do requirente

    private ItemManagement.Status status; // status
    private LocalDateTime creationDate; // data e horário da solicitação
}

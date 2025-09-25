package bl.tech.realiza.gateways.responses.dashboard;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import bl.tech.realiza.domains.enums.ContractTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DashboardDocumentDetailsResponseDto {
    private String branchName;
    private String branchCnpj;
    private String supplierName;
    private String supplierCnpj;
    private ContractTypeEnum contractType;
    private String subcontractorName;
    private String subcontractorCnpj;
    private String employeeFullName;
    private String employeePosition;
    private String employeeCbo;

    private Date contractStart;
    private Date contractFinish;
    private String serviceTypeName;
    private String responsibleFullName;
    private String responsibleEmail;
    private ContractStatusEnum contractStatus;

    private String documentTitle;
    private String documentSubgroupName;
    private String documentGroupName;
    private String documentType;
    private Boolean doesBlock;
    private Boolean adherent;
    private Boolean conforming;
    private Document.Status status;
    private LocalDateTime versionDate;
    private LocalDateTime lastCheck;
    private LocalDateTime expirationDate;
}

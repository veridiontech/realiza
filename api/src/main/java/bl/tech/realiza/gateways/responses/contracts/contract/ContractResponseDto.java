package bl.tech.realiza.gateways.responses.contracts.contract;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.*;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ContractResponseDto {
    // provider
    private String idContract;
    private String serviceType;
    private String serviceName;
    private String description;
    private String idResponsible;
    private String contractReference;
    private String responsible;
    private Contract.ExpenseType expenseType;
    private Boolean finished;
    private Date dateStart;
    private Date endDate;
    private Contract.IsActive isActive;
    private ContractStatusEnum status;
    private Boolean subcontractPermission;
    private String contractSupplierId;
    private List<String> activities;

    // client
    private String branchName;
    private String branch;

    // supplier
    private String providerSupplierName;
    private String providerSupplier;
    private String providerSupplierCnpj;

    // subcontractor
    private String providerSubcontractorName;
    private String providerSubcontractor;
}

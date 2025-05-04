package bl.tech.realiza.gateways.responses.contracts;

import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.Requirement;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ContractSupplierResponseDto {
    private String idContract;
    private String serviceType;
    private String serviceDuration;
    private String serviceName;
    private String description;
    private String idResponsible;
    private Contract.ExpenseType expenseType;
    private String contractReference;
    private Boolean subcontractPermission;
    private Date dateStart;
    private Contract.IsActive isActive;
    private List<String> activities;
    private String idBranch;
    private String nameBranch;
    private String idSupplier;
    private String nameSupplier;
}

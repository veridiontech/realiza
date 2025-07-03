package bl.tech.realiza.gateways.requests.contracts;

import bl.tech.realiza.domains.contract.Contract;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class ContractRequestDto {
    // contract
    private String serviceDuration;
    private String serviceName;
    private String contractReference;
    private String description;
    private String allocatedLimit;
    private String responsible;
    private Contract.ExpenseType expenseType;
    private Boolean labor;
    private Boolean hse;
    private Date startDate;
    private Date endDate;
    private String cnpj;

    // contract supplier
    private Boolean subcontractPermission;
    private String idServiceType;
    private List<String> idActivities;
    private String idBranch;
    private String idSupplierContract;
    private String idProviderSupplier; // also for subcontractor

    // subcontractor
    private String idProviderSubcontractor;
}

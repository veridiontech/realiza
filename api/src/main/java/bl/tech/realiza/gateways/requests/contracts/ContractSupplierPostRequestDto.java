package bl.tech.realiza.gateways.requests.contracts;

import bl.tech.realiza.domains.contract.Contract;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class ContractSupplierPostRequestDto {
    // contract
    private String serviceDuration;
    private String serviceName;
    private String contractReference;
    private String description;
    private String allocatedLimit;
    private String idResponsible;
    private Contract.ExpenseType expenseType;
    private Boolean labor;
    private Boolean hse;
    private Date dateStart;
    private String idServiceType;
    private String idRequester;
    private Boolean subcontractPermission;
    private List<String> activities;
    private String idBranch;
    private ProviderDatas providerDatas;

    @Data
    public static class ProviderDatas {
        private String corporateName;
        private String email;
        private String cnpj;
        private String telephone;
    }
}

package bl.tech.realiza.gateways.requests.contracts;

import bl.tech.realiza.domains.contract.Contract;
import lombok.*;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractSubcontractorPostRequestDto {
    // contract
    private String serviceName;
    private String contractReference;
    private String description;
    private Contract.ExpenseType expenseType;
    private Boolean labor;
    private Boolean hse;
    private Integer subcontractLevel;
    private Date dateStart;
    private String idRequester;
    private List<String> idActivities;
    private String idContractSupplier;

    private ContractSupplierPostRequestDto.ProviderDatas providerDatas;
}
